package com.linxiao.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.linxiao.framework.event.ExitAppEvent;
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
    protected String TAG;

    private List<BaseDataManager> listDataManagers;

    private boolean printLifeCycle = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (printLifeCycle) {
            Log.d(TAG, "onCreate");
        }
        TAG = this.getClass().getSimpleName();
        EventBus.getDefault().register(this);
        listDataManagers = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (printLifeCycle) {
            Log.d(TAG, "onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (printLifeCycle) {
            Log.d(TAG, "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (printLifeCycle) {
            Log.d(TAG, "onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (printLifeCycle) {
            Log.d(TAG, "onStop");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       if (printLifeCycle) {
           Log.d(TAG, "onRestart");
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printLifeCycle) {
            Log.d(TAG, "onDestroy");
        }
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

    /**
     * set is print activity life cycle
     * <p>if true, activity will log out life cycle</p>
     * */
    public void setPrintLifeCycle(boolean printLifeCycle) {
        this.printLifeCycle = printLifeCycle;
    }
}
