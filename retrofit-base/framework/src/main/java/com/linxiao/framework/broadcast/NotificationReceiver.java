package com.linxiao.framework.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.activity.NotificationResumeActivity;
import com.linxiao.framework.support.log.LogManager;
import com.linxiao.framework.support.notification.NotificationWrapper;

/**
 * 用于接收应用通知消息点击的事件并根据当前应用的状态做出处理
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogManager.LogPrinter logPrinter = LogManager.createLogPrinter(LogManager.DEBUG);
        logPrinter.Tag(TAG);
        logPrinter.append("Receive Notification Broadcast");
        Bundle notificationExtra = intent.getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
        if (notificationExtra == null) {
            logPrinter.append("notification extra is null, can't guide to target page");
            logPrinter.print();
            return;
        }
        if (BaseApplication.isMainProcessRunning()) {
            logPrinter.append("application state: running");
            Intent targetIntent = new Intent();
            String targetKey = notificationExtra.getString(NotificationWrapper.KEY_TARGET_ACTIVITY_NAME);
            if (TextUtils.isEmpty(targetKey)) {
                targetKey = NotificationResumeActivity.class.getName();
                logPrinter.append("target key is empty, application will back to front as default");
            }
            try {
                Class<?> targetActivityClass = Class.forName(targetKey);
                targetIntent.setClass(context, targetActivityClass);
                targetIntent.putExtras(notificationExtra);
            } catch (ClassNotFoundException e) {
                logPrinter.append("cant find target activity class: %s, application will back to front as default", targetKey);
                targetIntent.setClass(context, NotificationResumeActivity.class);
            }
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(targetIntent);
        }
        else {
            logPrinter.append("application state: not running");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, notificationExtra);
            context.startActivity(launchIntent);
        }
        logPrinter.print();
    }

}
