package com.linxiao.framework.support.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.broadcast.NotificationReceiver;

import java.util.List;

/**
 * 框架下常用通知构造类
 * <p>
 * 主要用于处理推送简易的文本，图片等信息，使用此类构建的Notification
 * 在没有设置点击事件时的操作为将App从后台唤起到前台，如果App未启动则启动App
 * </p>
 * Created by linxiao on 2016/12/8.
 */
public class SimpleNotificationBuilder {

    private static final String TAG = SimpleNotificationBuilder.class.getSimpleName();

    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent mPendingIntent;

    /**
     * 构造方法
     * <p>此处将Notification的icon设定为只用NotificationWrapper中所配置的默认icon</p>
     * */
    public SimpleNotificationBuilder(Context context, @NonNull String contentTitle, @NonNull String contentText) {
        this(context, NotificationWrapper.getDefaultIconRes(), contentTitle, contentText);
    }

    /**
     * 构造方法
     * <p>icon，contentTitle，contentText为构建一个Notification的必须参数，
     * 如果不传递这三个参数，代码不会报错，但通知不会显示</p>
     * */
    public SimpleNotificationBuilder(Context context, @DrawableRes int icon, @NonNull String contentTitle, @NonNull String contentText) {
        this.mContext = context;
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
    }

    /**
     * 对Builder中的NotificationCompat.Builder进行配置
     * <p>直接使用形参中传入的builder对象配置属性即可，主要用于对Builder对象的一些特殊配置的处理</p>
     * */
    public SimpleNotificationBuilder configureBuilder(@NonNull BuilderConfigurator configurator) {
        configurator.configure(mBuilder);
        return this;
    }

    /**
     * configure notification as following states:<br>
     * priority : default <br>
     * when : current time <br>
     * autoCancel : true <br>
     * other options : default <br>
     * */
    public SimpleNotificationBuilder configureNotificationAsDefault() {
        mBuilder.setWhen(System.currentTimeMillis())
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setPriority(Notification.PRIORITY_DEFAULT)
        .setAutoCancel(true);
        return this;
    }

    /**
     * add big content text to notification
     * @param title the notification title when expanded
     * @param contentText notification content text when expanded
     * @param summaryText can be seen as a subtitle, not very useful
     * */
    public SimpleNotificationBuilder setBigText(String title, String contentText, String summaryText) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(contentText);
        if (!TextUtils.isEmpty(summaryText)) {
            bigTextStyle.setSummaryText(summaryText);
        }
        setStyle(bigTextStyle);
        return this;
    }
    /**
     * add big content text to notification
     * @param title the notification title when expanded
     * @param contentText notification content text when expanded
     * */
    public SimpleNotificationBuilder setBigText(String title, String contentText) {
        setBigText(title, contentText, null);
        return this;
    }

    /**
     * 为通知添加大图内容
     * <p>由于Android的设定，图片高度最好不要超过256dp</p>
     * @param title 展开内容标题
     * @param picture 大图
     * @param summaryText 概要内容，可以看做二级标题，没啥用
     * */
    public SimpleNotificationBuilder setBigPicture(String title, Bitmap picture, String summaryText) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.bigPicture(picture);
        if (!TextUtils.isEmpty(summaryText)) {
            bigPictureStyle.setSummaryText(summaryText);
        }
        setStyle(bigPictureStyle);
        return this;
    }
    /**
     * 为通知添加大图内容
     * <p>由于Android的设定，图片高度最好不要超过256dp</p>
     * @param title 展开内容标题
     * @param picture 大图
     * */
    public SimpleNotificationBuilder setBigPicture(String title, Bitmap picture) {
        setBigPicture(title, picture, null);
        return this;
    }

    /**
     * 为通知设置多行消息内容
     * <p>
     * 注意内容过长时并不会自动换行，在低版本上并不会显示该样式
     * </p>
     * @param title 展开内容标题
     * @param summaryText 概要内容
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, String summaryText, String... lines) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        if (!TextUtils.isEmpty(summaryText)) {
            inboxStyle.setSummaryText(summaryText);
        }
        for (String line : lines) {
            inboxStyle.addLine(line);
        }
        setStyle(inboxStyle);
        return this;
    }
    /**
     * 为通知设置多行消息内容
     * @param title 展开内容标题
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, String... lines) {
        setInboxMessages(title, null, lines);
        return this;
    }
    /**
     * 为通知设置多行消息内容
     * @param title 展开内容标题
     * @param summaryText 概要内容
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, String summaryText, List<String> lines) {
        if (lines != null) {
            setInboxMessages(title, summaryText, lines.toArray(new String[]{}));
        }
        return this;
    }
    /**
     * 为通知设置多行消息内容
     * @param title 展开内容标题
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, List<String> lines) {
        setInboxMessages(title, null, lines);
        return this;
    }

    /**
     * 设置点击Notification时目标Activity的Intent
     * <p>默认处理逻辑为如果App的状态为运行中则直接打开目标Activity，如果App在后台则将App切换到前台
     * 如果App没有启动，则先启动App再打开目标Activity</p>
     * */
    public SimpleNotificationBuilder setTargetActivityIntent(Intent targetActivityIntent) {
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        if (targetActivityIntent.getExtras() != null) {
            bundle.putAll(targetActivityIntent.getExtras());
        }
        bundle.putString(NotificationWrapper.KEY_DEST_ACTIVITY_NAME, targetActivityIntent.getComponent().getClassName());
        broadcastIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, bundle);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return this;
    }

    /**
     * 设置点击Notification时目标Activity的Intent，并在打开时将其上一级Activity添加到回退栈
     * <p><strong>使用此方法打开Activity时，将清空当前App的TaskStack,无论App当时是否处于运行状态</strong>
     * <br>
     * <strong>注意：目标Activity要想在back时成功回退到指定的Activity必须在该Activity的manifest声明中添加
     *      <br>android:parentActivityName="回退Activity路径"<br>
     *  的属性，否则将不会生效
     * </strong></p>
     * */
    public SimpleNotificationBuilder setTargetActivityWithParentStack(Intent targetActivityIntent) {
        String className = targetActivityIntent.getComponent().getClassName();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        try {
            Class<?> sourceActivityClass = Class.forName(className);
            stackBuilder.addParentStack(sourceActivityClass);
            Log.d(TAG, "setDestWithParentStack: destActivityClass = " + sourceActivityClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        stackBuilder.addNextIntent(targetActivityIntent);
        mPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return this;
    }

    /**
     * 不走封装Builder类默认的处理逻辑自定义PendingIntent时使用
     * */
    public SimpleNotificationBuilder setCustomPendingIntent(PendingIntent pendingIntent) {
        mPendingIntent = pendingIntent;
        return this;
    }

    public SimpleNotificationBuilder setShowHangUp() {

        return this;
    }


    public NotificationOperator build() {
       return build(0);
    }

    public NotificationOperator build(int notifyId) {
        if (mPendingIntent == null) {
            Intent destIntent = new Intent(mContext, NotificationReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(mContext, 0, destIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder.setContentIntent(mPendingIntent);

        return new NotificationOperator(notifyId, mBuilder.build());
    }

    public Context getBuilderContext() {
        return mContext;
    }


    /*-----以下为代理NotificationCompat.Builder的常用方法，方便快速配置Notification-----*/

    /**
     * may not show on some low api machines
     * */
    public SimpleNotificationBuilder setSubText(String subText) {
        mBuilder.setSubText(subText);
        return this;
    }
    /**
     * looks like not take effect on systems higher than lollipop
     * */
    public SimpleNotificationBuilder setTicker(String tickerText) {
        mBuilder.setTicker(tickerText);
        return this;
    }

    public SimpleNotificationBuilder setPriority(int pri) {
        mBuilder.setPriority(pri);
        return this;
    }

    public SimpleNotificationBuilder setVisibility(int visibility) {
        mBuilder.setVisibility(visibility);
        return this;
    }

    /**
     * if called setContentInfo(), this method will not take effect
     * */
    public SimpleNotificationBuilder setNumber(int number) {
        mBuilder.setNumber(number);
        return this;
    }

    public SimpleNotificationBuilder setAutoCancel(boolean autoCancel) {
        mBuilder.setAutoCancel(autoCancel);
        return this;
    }

    public SimpleNotificationBuilder setOngoing(boolean ongoing) {
        mBuilder.setOngoing(ongoing);
        return this;
    }

    public SimpleNotificationBuilder setContentInfo(String info) {
        mBuilder.setContentInfo(info);
        return this;
    }

    public SimpleNotificationBuilder setLargeIcon(Bitmap icon) {
        mBuilder.setLargeIcon(icon);
        return this;
    }

    public SimpleNotificationBuilder setStyle(NotificationCompat.Style style) {
        mBuilder.setStyle(style);
        return this;
    }



}
