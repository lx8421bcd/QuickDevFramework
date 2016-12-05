package com.linxiao.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.support.notification.NotificationWrapper;

/**
 * 启动Activity基类
 * <p>执行App启动的预处理，此处用于执行框架模块的预处理操作。
 * </p>
 * Created by linxiao on 2016/12/5.
 */
public abstract class BaseSplashActivity extends BaseActivity {

    protected boolean isHandleNotification;
    private Bundle notificationExtra;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        notificationExtra = intent.getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
        isHandleNotification = notificationExtra != null;

    }

    protected void handleNotification() {
        if (!isHandleNotification) {
            return;
        }
        String destKey = notificationExtra.getString(NotificationWrapper.KEY_DEST_ACTIVITY_NAME);
        if (TextUtils.isEmpty(destKey)) {
            Log.e(TAG, "handleNotification: destKey is null !");
            return;
        }
        try {
            Class<?> destActivityClass = Class.forName(destKey);
            Intent destIntent = new Intent(this, destActivityClass);
            destIntent.putExtras(notificationExtra);
            startActivity(destIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "handleNotification: reflect to get activity class failed !");
        }
    }

}
