package com.linxiao.framework.architecture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.linxiao.framework.notification.NotificationManager;


/**
 * 启动Activity基类
 * <p>执行App启动的预处理，此处用于执行框架模块的预处理操作。
 * </p>
 * Created by linxiao on 2016/12/5.
 */
@SuppressLint("CustomSplashScreen")
public abstract class BaseSplashActivity extends BaseActivity {

    protected boolean isHandleNotification;
    private Bundle notificationExtra;
    protected SplashScreen splashScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        notificationExtra = intent.getBundleExtra(NotificationManager.KEY_NOTIFICATION_EXTRA);
        isHandleNotification = notificationExtra != null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleNotification();
    }

    protected void handleNotification() {
        if (!isHandleNotification) {
            return;
        }
        String targetKey = notificationExtra.getString(NotificationManager.KEY_TARGET_ACTIVITY_NAME);
        if (TextUtils.isEmpty(targetKey)) {
            Log.e(TAG, "handleNotification: target key is null !");
            return;
        }
        try {
            Class<?> destActivityClass = Class.forName(targetKey);
            Intent destIntent = new Intent(this, destActivityClass);
            destIntent.putExtras(notificationExtra);
            startActivity(destIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "handleNotification: reflect to get activity class failed !");
        }
    }

    protected boolean isInitFinished() {
        return true;
    }

    protected void keepSplashScreenUntilInitFinished() {
        // Set up an OnPreDrawListener to the root view.
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check if the initial data is ready.
                        if (isInitFinished()) {
                            // The content is ready; start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // The content is not ready; suspend.
                            return false;
                        }
                    }
                }
        );
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

}
