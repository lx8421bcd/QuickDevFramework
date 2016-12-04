package com.linxiao.framework.support.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 *
 * Created by linxiao on 2016/12/2.
 */
public class SimpleNotificationBuilder extends BaseNotificationBuilder {

    public SimpleNotificationBuilder(Context context, @NonNull String title, @NonNull String message, @NonNull Intent destIntent) {
        super(context, title, message, destIntent);
        mBuilder.setTicker(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setTicker(message)
                .setContentText(message);
    }
}
