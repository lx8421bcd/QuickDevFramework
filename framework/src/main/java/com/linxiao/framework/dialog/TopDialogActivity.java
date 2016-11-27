package com.linxiao.framework.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.linxiao.framework.R;
import com.linxiao.framework.activity.BaseActivity;

/**
 * 用于显示顶级Dialog，比如Service弹出Dialog
 * 这里采用限制死的方案，是传入文本和图片信息，以及简易对象信息。
 * Created by LinXiao on 2016-11-25.
 */
public class TopDialogActivity extends AppCompatActivity {

    public static final String KEY_DIALOG_TITLE = "dialog_title";
    public static final String KEY_DIALOG_MESSAGE = "dialog_message";

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
        String title = intent.getStringExtra(KEY_DIALOG_TITLE);
        String message = intent.getStringExtra(KEY_DIALOG_MESSAGE);
        if (message == null) {
            finish();
        }
        if (title != null) {
            alertDialogFragment.setTitle(title);
        }
        alertDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialogFragment.setMessage(message)
        .setPositiveButton(getString(R.string.framework_text_dialog_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0); //取消关闭Activity时的动画
    }
}
