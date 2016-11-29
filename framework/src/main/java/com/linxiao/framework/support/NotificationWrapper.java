package com.linxiao.framework.support;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.dialog.TopDialogActivity;

/**
 * 通知消息包装器
 * Created by LinXiao on 2016-11-27.
 */
public class NotificationWrapper {

    private static int defaultIconRes = 0;

    public static void setDefaultIcon(@DrawableRes int resId) {
        defaultIconRes = resId;
    }

    public static void sendNotification(Context context, int notificationId, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(defaultIconRes)
                .setContentTitle(BaseApplication.getApplicationName())
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);

        Intent resultIntent = new Intent(context, TopDialogActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(TopDialogActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }



}
