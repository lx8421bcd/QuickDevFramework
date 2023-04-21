package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.linxiao.framework.notification.NotificationManager;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentNotificationApiBinding;
import com.linxiao.framework.architecture.SimpleViewBindingFragment;

import java.util.Arrays;

public class NotificationApiFragment extends SimpleViewBindingFragment<FragmentNotificationApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().btnSendSimple.setOnClickListener(this::onSendNotificationClick);
        getViewBinding().btnSendBigText.setOnClickListener(this::onSendBigTextClick);
        getViewBinding().btnSendBigPicture.setOnClickListener(this::onSendBigPictureClick);
        getViewBinding().btnSendInbox.setOnClickListener(this::onSendInboxClick);
        getViewBinding().btnSendHangUp.setOnClickListener(this::onSendHangUpClick);
    }

    public void onSendNotificationClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("简单通知")
                .setContentText("这是一条简单的通知");
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getActivityPendingIntent(getActivity(), intent));
        NotificationManager.notify(123, builder);
    }

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
        builder.setContentIntent(NotificationManager.getActivityPendingIntent(getActivity(), intent));
        NotificationManager.notify(124, builder);
    }

    public void onSendBigPictureClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("bigPicture")
                .setContentText("一条bigPicture");
        NotificationManager.setBigPicture(builder,
                "big picture title",
                "bit picture summary",
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify));
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getActivityPendingIntent(getActivity(), intent));
        NotificationManager.notify(125, builder);
    }

    public void onSendInboxClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.create()
                .setContentTitle("inbox")
                .setContentText("一条inbox");
        NotificationManager.setInboxMessages(builder,
                "inbox title",
                "inbox summary",
                Arrays.asList("这是一行内容", "这是一行内容", "这是一行内容", "这是一行内容"));
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getActivityPendingIntent(getActivity(), intent));
        NotificationManager.notify(126, builder);
    }

    public void onSendHangUpClick(View v) {
        NotificationCompat.Builder builder = NotificationManager.createHangup("hangup")
                .setContentTitle("横幅通知")
                .setContentText("这是一条横幅通知");
        Intent intent = new Intent(getActivity(), NotificationTargetActivity.class);
        builder.setContentIntent(NotificationManager.getActivityPendingIntent(getActivity(), intent));
        NotificationManager.notify(126, builder);
    }

}
