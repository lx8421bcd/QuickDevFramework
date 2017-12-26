package com.linxiao.quickdevframework.sample.frameworkapi;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.QDFApplication;
import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.notification.NotificationManager;
import com.linxiao.quickdevframework.R;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationApiFragment extends BaseFragment {

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_notification_api;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
    }
    
    @OnClick(R.id.btnSendSimple)
    public void onSendNotificationClick(View v) {
        NotificationManager.sendSimpleNotification("简单通知", "这是一条简单的通知", new Intent(getActivity(), NotificationTargetActivity.class));
    }
    
    @OnClick(R.id.btnSendBigText)
    public void onSendBigTextClick(View v) {
        String bigText = "这条通知很长";
        for (int i = 0; i < 50; i++) {
            bigText += "很长";
        }
        NotificationManager.buildNotification(getContext(), "bigText", "一条bigText")
                .setBigText("big text title", bigText)
                .configureNotificationAsDefault()
                .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
                .send(1024);
    }
    
    @OnClick(R.id.btnSendBigPicture)
    public void onSendBigPictureClick(View v) {
        NotificationManager.buildNotification(getContext(), "bigPicture", "一条bigPicture")
                .setBigPicture("big picture title", BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify))
                .configureNotificationAsDefault()
                .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
                .send(1025);
    }
    
    @OnClick(R.id.btnSendInbox)
    public void onSendInboxClick(View v) {
        NotificationManager.buildNotification(getContext(), "inbox", "一条inbox")
                .setInboxMessages("inbox title", Arrays.asList("这是一行内容","这是一行内容","这是一行内容","这是一行内容"))
                .configureNotificationAsDefault()
                .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
                .send(1026);
    }
    
    @OnClick(R.id.btnSendHangUp)
    public void onSendHangUpClick(View v) {
        NotificationManager.buildNotification(getContext(), "横幅通知", "一条横幅通知")
                .configureNotificationAsDefault()
                .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
                .setHangUp(true)
                .send(1027);
    }

}
