package com.linxiao.quickdevframework.frameworkapi;

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

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.notification.NotificationWrapper;
import com.linxiao.framework.support.notification.SimpleNotificationBuilder;
import com.linxiao.quickdevframework.R;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationApiFragment extends BaseFragment {

    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_notification_api;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick(R.id.btnSendSimple)
    public void onSendNotificationClick(View v) {
        NotificationWrapper.sendSimpleNotification("简单通知", "这是一条简单的通知", new Intent(getActivity(), NotificationTargetActivity.class));
    }

    @OnClick(R.id.btnSendBigText)
    public void onSendBigTextClick(View v) {
        String bigText = "这条通知很长";
        for (int i = 0; i < 50; i++) {
            bigText += "很长";
        }
        NotificationWrapper.createSimpleNotificationBuilder(getContext(), "bigText", "一条bigText")
        .setBigText("big text title", bigText)
        .configureNotificationAsDefault()
        .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
        .build(1024)
        .send();
    }

    @OnClick(R.id.btnSendBigPicture)
    public void onSendBigPictureClick(View v) {
        NotificationWrapper.createSimpleNotificationBuilder(getContext(), "bigPicture", "一条bigPicture")
        .setBigPicture("big picture title", BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify))
        .configureNotificationAsDefault()
        .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
        .build(1025)
        .send();
    }

    @OnClick(R.id.btnSendBigPicture)
    public void onSendInboxClick(View v) {
        NotificationWrapper.createSimpleNotificationBuilder(getContext(), "inbox", "一条inbox")
        .setInboxMessages("inbox title", Arrays.asList("这是一行内容","这是一行内容","这是一行内容","这是一行内容"))
        .configureNotificationAsDefault()
        .setTargetActivityIntent(new Intent(getActivity(), NotificationTargetActivity.class))
        .build(1026)
        .send();
    }

    /*FIXME:目前横幅通知的处理方式其实是类似于电话的处理，有的系统显示横幅，有的则直接执行目标Intent在不同版本的系统上表现形式不同，并不完全是横幅，不推荐使用*/
    void hangup() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BaseApplication.getAppContext());
        builder.setContentTitle("横幅通知");
        builder.setContentText("请在设置通知管理中开启消息横幅提醒权限");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify));
        Intent intent = new Intent(BaseApplication.getAppContext(), NotificationTargetActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 1, intent, 0);
        builder.setContentIntent(pIntent);
        //这句是重点
        builder.setFullScreenIntent(pIntent, true);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManagerCompat.from(BaseApplication.getAppContext()).notify(462, notification);
    }

}
