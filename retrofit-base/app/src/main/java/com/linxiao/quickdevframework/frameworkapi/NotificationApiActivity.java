package com.linxiao.quickdevframework.frameworkapi;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.broadcast.NotificationReceiver;
import com.linxiao.framework.support.notification.NotificationWrapper;
import com.linxiao.framework.support.notification.SimpleNotificationBuilder;
import com.linxiao.quickdevframework.R;

public class NotificationApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_api);
    }

    public void onSendNotificationClick(View v) {
        NotificationWrapper.sendSimpleNotification("简单通知", "这是一条简单的通知", new Intent(this, ToastApiActivity.class));
    }

    public void onSendBigTextClick(View v) {
        String bigText = "这条通知很长";
        for (int i = 0; i < 30; i++) {
            bigText += "很长";
        }
        SimpleNotificationBuilder builder = NotificationWrapper.createSimpleNotificationBuilder(this, "bigText", "一条bigText");
        builder.setBigText("big text title", bigText)
                .configureNotificationAsDefault()
                .setTargetActivityIntent(new Intent(this, ToastApiActivity.class))
                .build(1024)
                .send();
    }


    void testBack() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify))
                .setContentTitle("test back stack")
                .setContentText("test back stack in app and app close");

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(NotificationWrapper.KEY_DEST_ACTIVITY_NAME, ToastApiActivity.class.getName());
        broadcastIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, bundle);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat.from(this).notify(1024, mBuilder.build());
    }

    void send() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        inboxStyle.setBigContentTitle("Event tracker details:");
        for (String event : events) {

            inboxStyle.addLine(event + "text");
        }
        mBuilder.setStyle(inboxStyle);
        NotificationManagerCompat.from(this).notify(45645, mBuilder.build());
    }

    void hangup() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BaseApplication.getAppContext());
        builder.setContentTitle("横幅通知");
        builder.setContentText("请在设置通知管理中开启消息横幅提醒权限");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify));
        Intent intent = new Intent(BaseApplication.getAppContext(), ToastApiActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);
        builder.setContentIntent(pIntent);
        //这句是重点
        builder.setFullScreenIntent(pIntent, true);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManagerCompat.from(BaseApplication.getAppContext()).notify(462, notification);
    }

}
