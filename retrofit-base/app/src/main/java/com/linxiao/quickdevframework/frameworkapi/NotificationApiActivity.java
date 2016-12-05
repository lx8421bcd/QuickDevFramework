package com.linxiao.quickdevframework.frameworkapi;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.broadcast.NotificationReceiver;
import com.linxiao.framework.support.notification.NotificationWrapper;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.SampleApplication;
import com.linxiao.quickdevframework.main.MainActivity;

public class NotificationApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_api);
    }

    public void onSendNotificationClick(View v) {
//        NotificationWrapper.sendNotification(this, 1024, "fucking fuck");
//        Intent resultIntent = new Intent(this, ToastApiActivity.class);
//        NotificationWrapper.sendSimpleNotification(this, 1024, "click to test toast", resultIntent);
//        send();
//        bigPictureStyle();
//        NotificationWrapper.buildSimpleNotification(this, "title", "you have an message", resultIntent)
//                .setResumeAppIfBackground()
//                .addToParentStack()
//                .send();
        testBack();
    }

    void testBack() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
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
        NotificationManagerCompat.from(this).notify(1024, mBuilder.build());
    }

    public void bigPictureStyle(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("BigPictureStyle");
        builder.setContentText("BigPicture演示示例");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("BigContentTitle");
        style.setSummaryText("SummaryText");
        style.bigPicture(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setStyle(style);
        builder.setAutoCancel(true);
        Intent intent = new Intent(this,ToastApiActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,1,intent,0);
        //设置点击大图后跳转
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(1234,notification);
    }


}
