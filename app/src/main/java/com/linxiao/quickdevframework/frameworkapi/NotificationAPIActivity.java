package com.linxiao.quickdevframework.frameworkapi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.NotificationWrapper;
import com.linxiao.quickdevframework.R;

public class NotificationApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_api);
    }

    public void onSendNotificationClick(View v) {
//        NotificationWrapper.sendNotification(this, 1024, "fucking fuck");
        Intent resultIntent = new Intent(this, ToastApiActivity.class);
        NotificationWrapper.sendSimpleNotification(this, 1024, "click to test toast", resultIntent);
//        send();

    }

    private void send() {
        Intent resultIntent = new Intent(this, ToastApiActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
//        stackBuilder.addParentStack(ToastApiActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("content")
                .setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1024, builder.build());
    }

}
