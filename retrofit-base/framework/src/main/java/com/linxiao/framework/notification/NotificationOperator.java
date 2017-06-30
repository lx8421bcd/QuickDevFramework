package com.linxiao.framework.notification;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

import com.linxiao.framework.BaseApplication;

/**
 * Notification 操作类，用于在Builder类创建Notification进行操作
 * Created by LinXiao on 2016-12-11.
 */
public class NotificationOperator {

    private int notifyId;
    private Notification mNotification;
    private Context mContext = BaseApplication.getAppContext();

    public NotificationOperator(Notification notification) {
        this.mNotification = notification;
    }

    public NotificationOperator(int notifyId, Notification notification) {
        this.notifyId = notifyId;
        this.mNotification = notification;
    }

    public void send() {
        NotificationManagerCompat.from(mContext).notify(notifyId, mNotification);
    }

    public void cancel() {
        NotificationManagerCompat.from(mContext).cancel(notifyId);
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public Notification getmNotification() {
        return mNotification;
    }
}
