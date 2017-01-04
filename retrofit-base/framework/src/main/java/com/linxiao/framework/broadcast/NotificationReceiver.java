package com.linxiao.framework.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.activity.NotificationResumeActivity;
import com.linxiao.framework.support.notification.NotificationWrapper;

/**
 * 用于接收应用通知消息点击的事件并根据当前应用的状态做出处理
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle notificationExtra = intent.getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
        if (notificationExtra == null) {
            return;
        }
        if (BaseApplication.isMainProcessRunning()) {
            Intent destIntent = new Intent();
            String destKey = notificationExtra.getString(NotificationWrapper.KEY_TARGET_ACTIVITY_NAME);
            if (TextUtils.isEmpty(destKey)) {
                destKey = NotificationResumeActivity.class.getName();
            }
            try {
                Class<?> destActivityClass = Class.forName(destKey);
                destIntent.setClass(context, destActivityClass);
                destIntent.putExtras(notificationExtra);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                destIntent.setClass(context, NotificationResumeActivity.class);
            }
            destIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(destIntent);
        }
        else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, notificationExtra);
            context.startActivity(launchIntent);
        }
    }

}
