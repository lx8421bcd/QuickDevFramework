package com.linxiao.quickdevframework.sample.frameworkapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.ApplicationUtil
import com.linxiao.framework.common.globalContext
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.FragmentApplicationApiBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Application类提供API示例
 * Created by linxiao on 2017/2/17.
 */
@SuppressLint("SetTextI18n")
class ApplicationApiFragment : SimpleViewBindingFragment<FragmentApplicationApiBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.ivAppIcon.setImageDrawable(ApplicationUtil.getAppIcon(globalContext.packageName))
        viewBinding.tvIsAppRunning.text = "${getString(R.string.is_app_running)}: ${ApplicationUtil.isAppForeground()}"
        viewBinding.tvIsAppForeground.text = "${getString(R.string.is_app_foreground)}: ${ApplicationUtil.isAppForeground()}"
        viewBinding.tvCPUName.text = "CPU Name: ${ApplicationUtil.getCPUName()}"
        viewBinding.btnGetAppName.setOnClickListener { v: View? ->
            viewBinding.tvAppName.text = ApplicationUtil.getAppName(globalContext.packageName)
        }
        viewBinding.btnGetAppVersion.setOnClickListener { v: View? ->
            val info = ApplicationUtil.getPackageInfo(globalContext.packageName)
            if (info != null) {
                viewBinding.tvAppVersion.text = info.versionName
            }
        }
        viewBinding.btnExitApp.setOnClickListener { v: View? ->
            ApplicationUtil.exitApplication(activity)
        }
        viewBinding.btnRestartApp.setOnClickListener { v: View? ->
            ApplicationUtil.restartApplication(activity)
        }
        viewBinding.btnGetSystemBootTime.setOnClickListener { v: View? ->
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault())
            viewBinding.tvSystemBootTime.text = format.format(Date(ApplicationUtil.systemBootTime))
        }
        viewBinding.tvExtraInfo.text = """
            AndroidId = ${ApplicationUtil.getAndroidID()}
        """.trimIndent()
    }
}
