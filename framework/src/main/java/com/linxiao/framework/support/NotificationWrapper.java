package com.linxiao.framework.support;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import android.support.v4.app.NotificationCompat;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.R;

/**
 * 通知消息包装器
 * Created by LinXiao on 2016-11-27.
 */
public class NotificationWrapper {

    private static int defaultIconRes = R.drawable.ic_notify_default;

    public static void setDefaultIcon(@DrawableRes int resId) {
        defaultIconRes = resId;
    }

    public static void getDefaultNotificationBuilder(Context context) {
        new NotificationCompat.Builder(context)
                .setSmallIcon(defaultIconRes)
                .setContentTitle(BaseApplication.getApplicationName());

    }

    /**
     * 发送简单的通知消息，通知消息的重点在消息内容
     *
     * @param context 上下文
     * @param nId     消息ID
     * @param message 消息内容
     */
    public static void sendSimpleNotification(Context context, int nId, String message, Intent resultIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(defaultIconRes)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(BaseApplication.getApplicationName())
                .setTicker(message)
                .setContentText(message);
//        resultIntent.setAction(Intent.ACTION_MAIN);
//        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(context.getClass());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        getNotificationManager(context).notify(nId, mBuilder.build());
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
