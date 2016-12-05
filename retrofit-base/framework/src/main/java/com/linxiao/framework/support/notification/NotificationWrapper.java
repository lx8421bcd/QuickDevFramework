package com.linxiao.framework.support.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.R;

/**
 * 通知消息包装器
 * Created by LinXiao on 2016-11-27.
 */
public class NotificationWrapper {
    private static final String TAG = NotificationWrapper.class.getSimpleName();

    public static String KEY_NOTIFICATION_EXTRA = "framework_notification_extra";
    public static final String KEY_DEST_ACTIVITY_NAME = "key_dest_name";

    private static int defaultIconRes = R.drawable.ic_notify_default;

    public static void setDefaultIconRes(@DrawableRes int resId) {
        defaultIconRes = resId;
    }

    @DrawableRes
    public static int getDefaultIconRes() {
        return defaultIconRes;
    }


    /**
     * 发送简单的通知消息，通知消息的重点在消息内容
     *
     * @param context  上下文
     * @param notifyId 消息ID
     * @param message  消息内容
     */
    public static void sendSimpleNotification(Context context, int notifyId, String message, Intent resultIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(defaultIconRes)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(BaseApplication.getApplicationName())
                .setTicker(message)
                .setContentText(message);

//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

//        Intent notificationIntent = new Intent(this, Main.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //关键两句
        resultIntent.setAction("android.intent.action.MAIN");
        resultIntent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(context.getClass());
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

//        getNotificationManager(context).notify(notifyId, mBuilder.build());
        NotificationManagerCompat.from(context).notify(notifyId, mBuilder.build());
    }


    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static SimpleNotificationBuilder buildSimpleNotification(Context context, String title, String message, Intent destIntent) {
        return new SimpleNotificationBuilder(context, title, message, destIntent);
    }

}
