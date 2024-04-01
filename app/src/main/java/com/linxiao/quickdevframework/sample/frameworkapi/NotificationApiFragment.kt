package com.linxiao.quickdevframework.sample.frameworkapi

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.notification.NotificationUtil.create
import com.linxiao.framework.notification.NotificationUtil.createHangup
import com.linxiao.framework.notification.NotificationUtil.getActivityPendingIntent
import com.linxiao.framework.notification.NotificationUtil.notify
import com.linxiao.framework.notification.NotificationUtil.setBigPicture
import com.linxiao.framework.notification.NotificationUtil.setBigText
import com.linxiao.framework.notification.NotificationUtil.setInboxMessages
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.FragmentNotificationApiBinding

class NotificationApiFragment : SimpleViewBindingFragment<FragmentNotificationApiBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnSendSimple.setOnClickListener { v: View? -> onSendNotificationClick(v) }
        viewBinding.btnSendBigText.setOnClickListener { v: View? -> onSendBigTextClick(v) }
        viewBinding.btnSendBigPicture.setOnClickListener { v: View? -> onSendBigPictureClick(v) }
        viewBinding.btnSendInbox.setOnClickListener { v: View? -> onSendInboxClick(v) }
        viewBinding.btnSendHangUp.setOnClickListener { v: View? -> onSendHangUpClick(v) }
    }

    fun onSendNotificationClick(v: View?) {
        val builder = create()
            .setContentTitle("简单通知")
            .setContentText("这是一条简单的通知")
        val intent = Intent(activity, NotificationTargetActivity::class.java)
        builder.setContentIntent(getActivityPendingIntent(activity, intent))
        notify(123, builder)
    }

    fun onSendBigTextClick(v: View?) {
        var bigText = "这条通知很长"
        for (i in 0..49) {
            bigText += "很长"
        }
        val builder = create()
            .setContentTitle("bigText")
            .setContentText("一条bigText")
        builder.setBigText("big text title", "big text summary", bigText)
        val intent = Intent(activity, NotificationTargetActivity::class.java)
        builder.setContentIntent(getActivityPendingIntent(activity, intent))
        notify(124, builder)
    }

    fun onSendBigPictureClick(v: View?) {
        val builder = create()
            .setContentTitle("bigPicture")
            .setContentText("一条bigPicture")
        builder.setBigPicture(
            "big picture title", "bit picture summary", BitmapFactory.decodeResource(
                resources, R.drawable.ic_notify
            )
        )
        val intent = Intent(activity, NotificationTargetActivity::class.java)
        builder.setContentIntent(getActivityPendingIntent(activity, intent))
        notify(125, builder)
    }

    fun onSendInboxClick(v: View?) {
        val builder = create()
            .setContentTitle("inbox")
            .setContentText("一条inbox")
        builder.setInboxMessages(
            "inbox title",
            "inbox summary",
            mutableListOf<String?>("这是一行内容", "这是一行内容", "这是一行内容", "这是一行内容")
        )
        val intent = Intent(activity, NotificationTargetActivity::class.java)
        builder.setContentIntent(getActivityPendingIntent(activity, intent))
        notify(126, builder)
    }

    fun onSendHangUpClick(v: View?) {
        val builder = createHangup("hangup")
            .setContentTitle("横幅通知")
            .setContentText("这是一条横幅通知")
        val intent = Intent(activity, NotificationTargetActivity::class.java)
        builder.setContentIntent(getActivityPendingIntent(activity, intent))
        notify(126, builder)
    }
}
