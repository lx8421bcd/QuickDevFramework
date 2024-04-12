package com.linxiao.quickdevframework.sample.widget

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.DensityHelper
import com.linxiao.framework.widget.TextDrawable
import com.linxiao.quickdevframework.databinding.FragmentWidgetsGuideBinding
import com.linxiao.quickdevframework.sample.mvvm.CaptchaActivity

/**
 *
 * @author lx8421bcd
 * @since 2017-02-12
 */
class WidgetsGuideFragment : SimpleViewBindingFragment<FragmentWidgetsGuideBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawable = TextDrawable.createRound()
        drawable.backgroundColor = Color.BLACK
        drawable.textColor = Color.WHITE
        drawable.text = "辣鸡"
        drawable.textSize = DensityHelper.dp2px(12f).toFloat()
        drawable.setPadding(4,4,4,4)
        viewBinding.ivTextDrawable.setImageDrawable(drawable)
        viewBinding.btnMVVMSample.setOnClickListener { v: View? -> onMVVMSampleClick(v) }
    }

    fun onMVVMSampleClick(v: View?) {
        startActivity(Intent(activity, CaptchaActivity::class.java))
    }
}
