package com.linxiao.framework.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
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
    public static int defaultIcon = R.drawable.ic_notify_default;
    private static String defaultTitle = " ";

    private NotificationManager() {
    }

    public static void setDefaultTitle(String defaultTitle) {
        NotificationManager.defaultTitle = defaultTitle;
    }

    public static void setDefaultIconRes(@DrawableRes int defaultIcon) {
        NotificationManager.defaultIcon = defaultIcon;
    }

    /**
     * get system NotificationManager
     *
     * @return instance of {@link NotificationManagerCompat}
     */
    public static NotificationManagerCompat getNotificationManager() {
        return NotificationManagerCompat.from(ContextProvider.get());
    }

    /**
     * check notification is closed by user
     */
    public static boolean isNotificationEnabled() {
        return getNotificationManager().areNotificationsEnabled();
    }

    /**
     * create a simple channel for app's notification if not exists
     * <p>
     * once channel are created, app cannot change it's config from the code.
     * channel's config can only changed by user manually
     * </p>
     *
     * @param channelId   channelId
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
     *
     * @param channelId   channelId
     * @param channelName channelName
     * @param importance  channelImportance, see importance constants in {@link NotificationManagerCompat}
     */
    public static void createChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat manager = getNotificationManager();
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
     *
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
        builder.setSmallIcon(defaultIcon)
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
            NotificationManagerCompat manager = getNotificationManager();
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
        builder.setSmallIcon(defaultIcon)
                .setContentTitle(defaultTitle)
                .setContentText(" ")
                .setFullScreenIntent(null, true)
                .setAutoCancel(true);
        return builder;
    }

    /**
     * set big content text for notification
     *
     * @param builder     builder
     * @param title       the notification title when expanded
     * @param contentText notification content text when expanded
     * @param summaryText can be seen as a subtitle, not very useful
     */
    public static NotificationCompat.Builder setBigText(
            NotificationCompat.Builder builder,
            String title,
            String summaryText,
            String contentText
    ) {
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
     *
     * @param builder     builder
     * @param title       title
     * @param picture     picture, the picture should not higher than 255dp
     * @param summaryText summary
     */
    public static NotificationCompat.Builder setBigPicture(
            NotificationCompat.Builder builder,
            String title,
            String summaryText,
            Bitmap picture
    ) {
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
     *
     * @param builder     builder
     * @param title       title
     * @param summaryText summary
     * @param lines       multiline text list
     */
    public static NotificationCompat.Builder setInboxMessages(
            NotificationCompat.Builder builder,
            String title,
            String summaryText,
            List<String> lines
    ) {
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

    public static PendingIntent getActivityPendingIntent(Context context, Intent intent) {
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    /**
     * show notification
     *
     * @param notifyId id
     * @param builder  builder
     */
    public static void notify(int notifyId, NotificationCompat.Builder builder) {
        if (builder == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(ContextProvider.get(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "notify: Manifest.permission.POST_NOTIFICATIONS not granted");
            return;
        }
        getNotificationManager().notify(notifyId, builder.build());
    }

    /**
     * cancel notification by id
     *
     * @param notifyId notificationId
     */
    public static void cancel(int notifyId) {
        getNotificationManager().cancel(notifyId);
    }

}
