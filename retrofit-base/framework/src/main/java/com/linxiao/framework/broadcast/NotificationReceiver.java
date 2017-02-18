package com.linxiao.framework.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.support.notification.NotificationResumeActivity;
import com.linxiao.framework.support.log.Logger;
import com.linxiao.framework.support.notification.NotificationWrapper;

/**
 * 用于接收应用通知消息点击的事件并根据当前应用的状态做出处理
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.LogPrinter logPrinter = Logger.createLogPrinter(Logger.DEBUG);
        logPrinter.tag(TAG);
        logPrinter.appendLine("Receive Notification Broadcast");
        Bundle notificationExtra = intent.getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
        if (notificationExtra == null) {
            logPrinter.appendLine("notification extra is null, can't guide to target page");
            logPrinter.print();
            return;
        }
        if (BaseApplication.isAppRunning()) {
            logPrinter.appendLine("application state: running");
            Intent targetIntent = new Intent();
            String targetKey = notificationExtra.getString(NotificationWrapper.KEY_TARGET_ACTIVITY_NAME);
            if (TextUtils.isEmpty(targetKey)) {
                targetKey = NotificationResumeActivity.class.getName();
                logPrinter.appendLine("target key is empty, application will back to front as default");
            }
            try {
                Class<?> targetActivityClass = Class.forName(targetKey);
                targetIntent.setClass(context, targetActivityClass);
                targetIntent.putExtras(notificationExtra);
            } catch (ClassNotFoundException e) {
                logPrinter.appendLine("cant find target activity class: %s, application will back to front as default", targetKey);
                targetIntent.setClass(context, NotificationResumeActivity.class);
            }
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(targetIntent);
        }
        else {
            logPrinter.appendLine("application state: not running");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(
                    BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, notificationExtra);
            context.startActivity(launchIntent);
        }
        logPrinter.print();
    }

}
