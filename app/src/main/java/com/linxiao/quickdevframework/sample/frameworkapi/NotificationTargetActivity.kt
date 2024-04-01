package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingActivity
import com.linxiao.quickdevframework.databinding.ActivityNotificationTargetBinding

class NotificationTargetActivity : SimpleViewBindingActivity<ActivityNotificationTargetBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.tvTargetDesc.setOnClickListener { v: View? -> finish() }
    }

}
