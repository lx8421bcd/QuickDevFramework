package com.linxiao.framework.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.linxiao.framework.R;
import com.linxiao.framework.common.ContextProvider;

import java.util.List;

/**
 * 通知消息包装器
 * Created by linxiao on 2016-11-27.
 */
public class NotificationManager {
    private static final String TAG = NotificationManager.class.getSimpleName();

    public static String KEY_NOTIFICATION_EXTRA = "framework_notification_extra";
    public static final String KEY_TARGET_ACTIVITY_NAME = "key_dest_name";

    public static final int DEFAULT_ICON = R.drawable.ic_notify_default;
    private static String defaultTitle = ContextProvider.get().getString(R.string.app_name);

    private NotificationManager() {}

    /**
     * get system NotificationManager
     * @return instance of {@link NotificationManagerCompat}
     */
    public static NotificationManagerCompat getManager() {
        return NotificationManagerCompat.from(ContextProvider.get());
    }

    /**
     * check notification is closed by user
     */
    public static boolean isNotificationEnabled() {
        return getManager().areNotificationsEnabled();
    }

    /**
     * create a simple channel for app's notification if not exists
     * <p>
     * once channel are created, app cannot change it's config from the code.
     * channel's config can only changed by user manually
     * </p>
     * @param channelId channelId
     * @param channelName channelName
     */
    public static void createChannel(String channelId, String channelName) {
        createChannel(channelId, channelName, NotificationManagerCompat.IMPORTANCE_DEFAULT);
    }

    /**
     * create a simple channel for app's notification if not exists
     * <p>
     * once channel are created, app cannot change it's config from the code.
     * channel's config can only changed by user manually
     * </p>
     * @param channelId channelId
     * @param channelName channelName
     * @param importance channelImportance, see importance constants in {@link NotificationManagerCompat}
     */
    public static void createChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat manager = getManager();
        NotificationChannel mChannel = manager.getNotificationChannel(channelId);
        if (mChannel != null) {
            return;
        }
        mChannel = new NotificationChannel(channelId, channelName, importance);
        mChannel.setSound(null, null);
        manager.createNotificationChannel(mChannel);
    }

    /**
     * create a notification safe builder
     * <p>
     * ensure the created notification will show normally and won't trigger crash.
     * put default notification channel to adapt Android O+
     * </p>
     * @return instance of {@link NotificationCompat.Builder}
     */
    public static NotificationCompat.Builder create() {
        return create("default");
    }

    /**
     * create a notification safe builder
     * <p>
     * ensure the created notification will show normally and won't trigger crash.
     * put default notification channel for Android O and after.
     * auto cancel.
     * </p>
     *
     * @param channelName channelName
     * @return instance of {@link NotificationCompat.Builder}
     */
    public static NotificationCompat.Builder create(String channelName) {
        createChannel(channelName, channelName);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ContextProvider.get(), channelName);
        builder.setSmallIcon(DEFAULT_ICON)
                .setContentTitle(defaultTitle)
                .setContentText(" ")
                .setAutoCancel(true);
        return builder;
    }

    /**
     * create a hangup notification builder
     * <p>
     * ensure the created notification will show normally and won't trigger crash.
     * put default notification channel for Android O and after.
     * auto cancel.
     * </p>
     *
     * @param channelName channelName
     * @return instance of {@link NotificationCompat.Builder}
     */
    public static NotificationCompat.Builder createHangup(String channelName) {
        // to ensure the hangup style show normally,
        // channel importance must set higher than IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat manager = getManager();
            NotificationChannel mChannel = manager.getNotificationChannel(channelName);
            if (mChannel == null) {
                mChannel = new NotificationChannel(channelName, channelName,
                        android.app.NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(null, null);
                manager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ContextProvider.get(), channelName);
        builder.setSmallIcon(DEFAULT_ICON)
                .setContentTitle(defaultTitle)
                .setContentText(" ")
                .setFullScreenIntent(null, true)
                .setAutoCancel(true);
        return builder;
    }

    /**
     * set big content text for notification
     * @param builder builder
     * @param title the notification title when expanded
     * @param contentText notification content text when expanded
     * @param summaryText can be seen as a subtitle, not very useful
     * */
    public static NotificationCompat.Builder setBigText(NotificationCompat.Builder builder,
                                                        String title,
                                                        String summaryText,
                                                        String contentText) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(contentText);
        if (!TextUtils.isEmpty(summaryText)) {
            bigTextStyle.setSummaryText(summaryText);
        }
        builder.setStyle(bigTextStyle);
        return builder;
    }

    /**
     * set big picture style for notification
     * @param builder builder
     * @param title title
     * @param picture picture, the picture should not higher than 255dp
     * @param summaryText summary
     * */
    public static NotificationCompat.Builder setBigPicture(NotificationCompat.Builder builder,
                                                    String title,
                                                    String summaryText,
                                                    Bitmap picture) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.bigPicture(picture);
        if (!TextUtils.isEmpty(summaryText)) {
            bigPictureStyle.setSummaryText(summaryText);
        }
        builder.setStyle(bigPictureStyle);
        return builder;
    }

    /**
     * set inbox data style for notification
     * @param builder builder
     * @param title title
     * @param summaryText summary
     * @param lines multiline text list
     * */
    public static NotificationCompat.Builder setInboxMessages(NotificationCompat.Builder builder,
                                                       String title,
                                                       String summaryText,
                                                       List<String> lines) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        if (!TextUtils.isEmpty(summaryText)) {
            inboxStyle.setSummaryText(summaryText);
        }
        for (String line : lines) {
            inboxStyle.addLine(line);
        }
        builder.setStyle(inboxStyle);
        return builder;
    }


    /**
     * show notification
     * @param notifyId id
     * @param builder builder
     */
    public static void show(int notifyId, NotificationCompat.Builder builder) {
        if (builder == null) {
            return;
        }
        getManager().notify(notifyId, builder.build());
    }

    /**
     * cancel notification by id
     * @param notifyId notificationId
     */
    public static void cancel(int notifyId) {
        getManager().cancel(notifyId);
    }


    /**
     * a simple notification intent
     *
     */
    public static void createResumeIntent() {

    }

    /**
     * 将Notification的Intent转换成用广播传递的Intent
     * <p>
     * 主要用于自定义Notification时处理点击打开Activity的事件，使用此方法
     * 将会在应用启动时直接打开目标Activity，应用未启动时先启动应用再打开Activity
     * </p>
     * */
    public static PendingIntent getBroadcastIntent(Context context, Intent targetActivityIntent) {
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        if (targetActivityIntent.getExtras() != null) {
            bundle.putAll(targetActivityIntent.getExtras());
        }
        if (targetActivityIntent.getComponent() != null) {
            bundle.putString(KEY_TARGET_ACTIVITY_NAME,
                    targetActivityIntent.getComponent().getClassName());
        }
        broadcastIntent.putExtra(NotificationManager.KEY_NOTIFICATION_EXTRA, bundle);
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static void sendNotification(Context context, int notifyId, Notification notification) {
        NotificationManagerCompat.from(context).notify(notifyId, notification);
    }

}
