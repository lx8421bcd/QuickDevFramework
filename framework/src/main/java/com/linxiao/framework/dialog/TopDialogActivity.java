package com.linxiao.framework.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.linxiao.framework.R;

import java.util.Map;

/**
 * 显示顶级Dialog，比如Service弹出Dialog
 * Created by linxiao on 2016-11-25.
 */
public class TopDialogActivity extends AppCompatActivity {

    public static final String KEY_TRANSMITTER_ID = "transmitter_id";
    /**
     * 由于无法向Activity传递闭包对象，因此采用静态缓存
     * */
    private static final Map<String, AlertTransmitter> transmitterCacheMap = new ArrayMap<>();

    private static final String DIALOG_TAG = "AlertDialogFragment";

    AlertDialogFragment alertDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogFragment(savedInstanceState);
    }

    private void initDialogFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            alertDialogFragment = (AlertDialogFragment) getSupportFragmentManager().getFragment(savedInstanceState, DIALOG_TAG);
        }
        alertDialogFragment = alertDialogFragment == null ? AlertDialogFragment.newInstance() : alertDialogFragment;

        Intent intent = getIntent();
        String transmitterKey = intent.getStringExtra(KEY_TRANSMITTER_ID);
        if (TextUtils.isEmpty(transmitterKey)) {
            throw new RuntimeException("DialogActivity initialize failed:" +
                    " has no available key to get Transmitter cache." +
                    " please check the key you put in intent");
        }
        AlertTransmitter transmitter = transmitterCacheMap.get(transmitterKey);
        if (transmitter == null) {
            finish();
            return;
        } else {
            transmitterCacheMap.remove(transmitterKey); //移除静态Map缓存
        }
        alertDialogFragment.setMessage(transmitter.getMessage());
        if (!TextUtils.isEmpty(transmitter.getTitle())) {
            alertDialogFragment.setTitle(transmitter.getTitle());
        }
        if (transmitter.getIcon() != null) {
            alertDialogFragment.setIcon(transmitter.getIcon());
        }
        String positiveText = transmitter.getPositiveText();
        if (TextUtils.isEmpty(positiveText)) {
            positiveText = getString(R.string.framework_text_dialog_confirm);
        }
        if (transmitter.getPositiveListener() == null) {
            alertDialogFragment.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            alertDialogFragment.setPositiveButton(
                    positiveText, transmitter.getPositiveListener());
        }
        if (transmitter.getNegativeListener() != null) {
            String negativeText = transmitter.getNegativeText();
            if (TextUtils.isEmpty(negativeText)) {
                negativeText = getString(R.string.framework_text_dialog_cancel);
            }
            alertDialogFragment.setNegativeButton(negativeText, transmitter.getNegativeListener());
        }
        if (!transmitter.isCancelable()) {
            alertDialogFragment.setCancelable(false);
        }
        alertDialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alertDialogFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialogFragment.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0); //取消关闭Activity时的动画
        super.onPause();
    }

    /**
     * 向AlertDialogActivity添加闭包传输对象
     * <p>
     * <strong>请将key通过Intent在启动DialogActivity时发送，确保正常使用</strong>
     * </p>
     * @param key 闭包对象查找key
     * @param transmitter 闭包对象
     * */
    static void addAlertDataTransmitter(String key, AlertTransmitter transmitter) {
        transmitterCacheMap.put(key, transmitter);
    }
}
