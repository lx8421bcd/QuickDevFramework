package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.ToastAlert
import com.linxiao.framework.common.ToastAlert.enqueue
import com.linxiao.framework.common.ToastAlert.show
import com.linxiao.quickdevframework.databinding.FragmentToastApiBinding
import com.squareup.leakcanary.core.R

class ToastApiFragment : SimpleViewBindingFragment<FragmentToastApiBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnTextShow.setOnClickListener { v: View? -> onBtnTextShowClick(v) }
        viewBinding.btnTextIconShow.setOnClickListener { v: View? -> onBtnTextIconShowClick(v) }
        viewBinding.btnTextEnqueue.setOnClickListener { v: View? -> onBtnTextEnqueueClick(v) }
        viewBinding.btnTextIconEnqueue.setOnClickListener { v: View? ->
            onBtnTextIconEnqueueClick(v)
        }
        viewBinding.btnPowerful.setOnClickListener { v: View? -> onBtnPowerfulClick(v) }
    }

    fun onBtnTextShowClick(v: View?) {
        show("show a text toast")
    }

    fun onBtnTextIconShowClick(v: View?) {
        show("show a text toast with a icon", R.drawable.leak_canary_icon)
    }

    fun onBtnTextEnqueueClick(v: View?) {
        show("enqueue a text toast")
    }

    fun onBtnTextIconEnqueueClick(v: View?) {
        enqueue("enqueue a text toast with a icon", R.drawable.leak_canary_icon)
    }

    fun onBtnPowerfulClick(v: View?) {
        ToastAlert.create("powerful").apply {
            iconResId = R.drawable.leak_canary_icon
            duration = 100
            gravity = Gravity.TOP
            offsetY = 200
        }.show()
    }
}
