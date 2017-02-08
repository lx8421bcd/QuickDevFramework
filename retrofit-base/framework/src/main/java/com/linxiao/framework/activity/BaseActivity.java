package com.linxiao.framework.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.linxiao.framework.R;
import com.linxiao.framework.dialog.AlertDialogFragment;
import com.linxiao.framework.event.ExitAppEvent;
import com.linxiao.framework.event.ShowAlertDialogEvent;
import com.linxiao.framework.manager.BaseDataManager;
import com.linxiao.framework.support.permission.PermissionWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * base activity class of entire project
 * <p>template for activities in the project, used to define common methods of activity </p>
 * */
public abstract class BaseActivity extends AppCompatActivity {

    protected static String TAG;

    private List<BaseDataManager> listDataManagers;

    private boolean isResumed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        EventBus.getDefault().register(this);
        listDataManagers = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResumed = false;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        isResumed = false;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        isResumed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        for (BaseDataManager dataManager : listDataManagers) {
            if ( dataManager == null) {
                continue;
            }
            dataManager.cancelAllCalls();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionWrapper.onSysAlertPermissionResult(this, requestCode);
        PermissionWrapper.onWriteSysSettingsPermissionResult(this, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionWrapper.handleCallback(this, requestCode, permissions, grantResults);
    }

    /**
     * bind DataManager to Activity life cycle, all network request will be canceled
     * when the activity is destroyed
     * */
    protected void bindDataManager(@NonNull BaseDataManager dataManager) {
        listDataManagers.add(dataManager);
    }

    /**
     * use this method instead of findViewById() to simplify view initialization <br>
     * it's not unchecked because T extends View
     * */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);

    }

    /**
     * use this method instead of findViewById() to simplify view initialization <br>
     * it's not unchecked because T extends View
     * */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int resId, View containerView) {
        return (T) containerView.findViewById(resId);
    }

    /**
     * get broadcast from EventBus, finish self to achieve close application
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitAppEvent(ExitAppEvent event) {
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityDialogEvent(ShowAlertDialogEvent event) {
        if (!isResumed) {
           return;
        }
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance();
        dialogFragment.setMessage(event.getMessage());
        if (!TextUtils.isEmpty(event.getTitle())) {
            dialogFragment.setTitle(event.getTitle());
        }
        if (event.getIcon() != null) {
            dialogFragment.setIcon(event.getIcon());
        }
        String positiveText = event.getPositiveText();
        if (TextUtils.isEmpty(positiveText)) {
            positiveText = getString(R.string.framework_text_dialog_confirm);
        }
        if (event.getPositiveListener() == null) {
            dialogFragment.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            dialogFragment.setPositiveButton(
                    positiveText, event.getPositiveListener());
        }
        if (event.getNegativeListener() != null) {
            String negativeText = event.getNegativeText();
            if (TextUtils.isEmpty(negativeText)) {
                negativeText = getString(R.string.framework_text_dialog_cancel);
            }
            dialogFragment.setNegativeButton(negativeText, event.getNegativeListener());
        }
        if (!event.isCancelable()) {
            dialogFragment.setCancelable(false);
        }
        dialogFragment.show(getSupportFragmentManager(), "AlertDialogFragment");
    }

}
