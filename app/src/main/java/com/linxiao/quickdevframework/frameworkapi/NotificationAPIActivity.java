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

    }


}
