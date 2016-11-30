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
import com.linxiao.framework.R;
import com.linxiao.framework.dialog.TopDialogActivity;

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


    public static void sendNotification(Context context, int notificationId, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("通知栏标题 ")
                .setContentText("通知内容")
//              .setContentIntent(((Activity)context).getDefaultIntent(Notification.FLAG_AUTO_CANCEL))//点击意图
                .setTicker("您有新的消息")
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)//用户点击就自动消失
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(defaultIconRes);//设置通知小ICON

        Intent resultIntent = new Intent(context, TopDialogActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(TopDialogActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
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
