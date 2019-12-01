package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.NotificationCompat;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.framework.notification.NotificationManager;
import com.linxiao.quickdevframework.R;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationApiFragment extends BaseFragment {

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_notification_api, container);
        ButterKnife.bind(this, getContentView());
    }

    @OnClick(R.id.btnSendSimple)
    public void onSendNotificationClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("简单通知")
                .setContentText("这是一条简单的通知");
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getBroadcastIntent(getActivity(), intent));
        NotificationManager.show(123, builder);
    }

    @OnClick(R.id.btnSendBigText)
    public void onSendBigTextClick(View v) {
        String bigText = "这条通知很长";
        for (int i = 0; i < 50; i++) {
            bigText += "很长";
        }
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("bigText")
                .setContentText("一条bigText");
        NotificationManager.setBigText(builder,
                "big text title",
                "big text summary",
                bigText);
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getBroadcastIntent(getActivity(), intent));
        NotificationManager.show(124, builder);
    }

    @OnClick(R.id.btnSendBigPicture)
    public void onSendBigPictureClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("bigPicture")
                .setContentText("一条bigPicture");
        NotificationManager.setBigPicture(builder,
                "big picture title",
                "bit picture summary",
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify));
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getBroadcastIntent(getActivity(), intent));
        NotificationManager.show(125, builder);
    }

    @OnClick(R.id.btnSendInbox)
    public void onSendInboxClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("inbox")
                .setContentText("一条inbox");
        NotificationManager.setInboxMessages(builder,
                "inbox title",
                "inbox summary",
                Arrays.asList("这是一行内容", "这是一行内容", "这是一行内容", "这是一行内容"));
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getBroadcastIntent(getActivity(), intent));
        NotificationManager.show(126, builder);
    }

    @OnClick(R.id.btnSendHangUp)
    public void onSendHangUpClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.createHangup("hangup")
                .setContentTitle("横幅通知")
                .setContentText("这是一条横幅通知");
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getBroadcastIntent(getActivity(), intent));
        NotificationManager.show(126, builder);
    }

}
