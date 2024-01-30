package com.linxiao.framework.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.linxiao.framework.R
import com.linxiao.framework.common.globalContext

/**
 * 通知消息包装器
 * Created by linxiao on 2016-11-27.
 */
object NotificationUtil {

    private val TAG = NotificationUtil::class.java.simpleName

    private var defaultIconRes = R.drawable.ic_notification_default
        set(value) {
            if (value != 0) {
                field = value
            }
        }

    private var defaultTitle = " "
        set(value) {
            if (value.isNotEmpty()) {
                field = value
            }
        }

    /**
     * get system NotificationManager
     *
     * @return instance of [NotificationManagerCompat]
     */
    val notificationManager by lazy {
        return@lazy NotificationManagerCompat.from(globalContext)
    }

    /**
     * check notification is closed by user
     */
    @JvmStatic
    fun isNotificationEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * create a simple channel for app's notification if not exists
     *
     * once channel are created, app cannot change it's config from the code.
     * channel's config can only changed by user manually
     *
     * @param channelId   channelId
     * @param channelName channelName
     * @param importance  channelImportance, see importance constants in [NotificationManagerCompat]
     */
    @JvmStatic
    @JvmOverloads
    fun createChannel(
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        sound: Uri? = null,
        audioAttributes: AudioAttributes? = null
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        var channel = notificationManager.getNotificationChannel(channelId)
        if (channel == null) {
            channel = NotificationChannel(channelId, channelName, importance)
        }
        channel.name = channelName
        channel.setSound(sound, audioAttributes)
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * delete notification channel
     */
    @JvmStatic
    fun deleteChannel(channelId: String) {
        notificationManager.deleteNotificationChannel(channelId)
    }

    /**
     * create a notification safe builder
     *
     * ensure the created notification will show normally and won't trigger crash.
     * put default notification channel for Android O and after.
     * auto cancel.
     *
     * @param channelId channelId
     * @param channelName channelName
     * @return instance of [NotificationCompat.Builder]
     */
    @JvmStatic
    @JvmOverloads
    fun create(
        channelId: String = "default",
        channelName: String = channelId
    ): NotificationCompat.Builder {
        createChannel(channelId, channelName)
        val builder = NotificationCompat.Builder(globalContext, channelId)
        builder.setSmallIcon(defaultIconRes)
            .setContentTitle(defaultTitle)
            .setContentText(" ")
            .setAutoCancel(true)
        return builder
    }

    /**
     * create a hangup notification builder
     *
     *
     * ensure the created notification will show normally and won't trigger crash.
     * put default notification channel for Android O and after.
     * auto cancel.
     *
     *
     * @param channelId channelId
     * @param channelName channelName
     * @return instance of [NotificationCompat.Builder]
     */
    @JvmStatic
    @JvmOverloads
    fun createHangup(
        channelId: String,
        channelName: String = channelId
    ): NotificationCompat.Builder {
        // to ensure the hangup style show normally,
        // channel importance must set higher than IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = notificationManager.getNotificationChannel(channelName)
            if (channel == null) {
                channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                channel.setSound(null, null)
                notificationManager.createNotificationChannel(channel)
            }
        }
        val builder = NotificationCompat.Builder(globalContext, channelName)
        builder.setSmallIcon(defaultIconRes)
            .setContentTitle(defaultTitle)
            .setContentText(" ")
            .setFullScreenIntent(null, true)
            .setAutoCancel(true)
        return builder
    }

    /**
     * set big content text for notification
     *
     * @param title       the notification title when expanded
     * @param contentText notification content text when expanded
     * @param summaryText can be seen as a subtitle, not very useful
     */
    @JvmStatic
    fun NotificationCompat.Builder.setBigText(
        title: String?,
        summaryText: String?,
        contentText: String?
    ): NotificationCompat.Builder {
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(title)
        bigTextStyle.bigText(contentText)
        if (!TextUtils.isEmpty(summaryText)) {
            bigTextStyle.setSummaryText(summaryText)
        }
        this.setStyle(bigTextStyle)
        return this
    }

    /**
     * set big picture style for notification
     *
     * @param title       title
     * @param picture     picture, the picture should not higher than 255dp
     * @param summaryText summary
     */
    @JvmStatic
    fun NotificationCompat.Builder.setBigPicture(
        title: String?,
        summaryText: String?,
        picture: Bitmap?
    ): NotificationCompat.Builder {
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.bigPicture(picture)
        if (!TextUtils.isEmpty(summaryText)) {
            bigPictureStyle.setSummaryText(summaryText)
        }
        this.setStyle(bigPictureStyle)
        return this
    }

    /**
     * set inbox data style for notification
     *
     * @param title       title
     * @param summaryText summary
     * @param lines       multiline text list
     */
    @JvmStatic
    fun NotificationCompat.Builder.setInboxMessages(
        title: String?,
        summaryText: String?,
        lines: List<String?>
    ): NotificationCompat.Builder {
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(title)
        if (!TextUtils.isEmpty(summaryText)) {
            inboxStyle.setSummaryText(summaryText)
        }
        for (line in lines) {
            inboxStyle.addLine(line)
        }
        this.setStyle(inboxStyle)
        return this
    }

    @JvmStatic
    fun getActivityPendingIntent(context: Context?, intent: Intent?): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * show notification
     *
     * @param notifyId id
     * @param builder  builder
     */
    @JvmStatic
    fun notify(notifyId: Int, builder: NotificationCompat.Builder?) {
        if (builder == null) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                globalContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "notify: Manifest.permission.POST_NOTIFICATIONS not granted")
            return
        }
        notificationManager.notify(notifyId, builder.build())
    }

    /**
     * cancel notification by id
     *
     * @param notifyId notificationId
     */
    @JvmStatic
    fun cancel(notifyId: Int) {
        notificationManager.cancel(notifyId)
    }
}