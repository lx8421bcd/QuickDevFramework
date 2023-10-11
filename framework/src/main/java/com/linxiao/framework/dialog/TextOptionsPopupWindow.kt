package com.linxiao.framework.dialog

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.linxiao.framework.common.DensityHelper
import com.linxiao.framework.common.getStatusBarHeight
import com.linxiao.framework.common.getUsableScreenWidth
import com.linxiao.framework.databinding.PopupTextOptionsBinding

/**
 * 单列表PopupWindow
 *
 * @author lx8421bcd
 * Create on 2017-06-19
 */
class TextOptionsPopupWindow(context: Context) : PopupWindow(context) {

    private val viewBinding by lazy {
        PopupTextOptionsBinding.inflate(LayoutInflater.from(context))
    }

    val adapter: TextOptionsAdapter by lazy {
        TextOptionsAdapter()
    }

    init {
        contentView = viewBinding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        setBackgroundDrawable(BitmapDrawable())
        adapter.setOnItemClickListener { itemView, position ->
            dismiss()
        }
        viewBinding.rcvList.layoutManager = LinearLayoutManager(context)
        viewBinding.rcvList.adapter = adapter
    }

    fun setWindowWidth(windowWidth: Int): TextOptionsPopupWindow {
        width = windowWidth + DensityHelper.dp2px(8f)
        return this
    }

    fun show(anchor: View) {
        val xy = intArrayOf(0, 0)
        anchor.getLocationInWindow(xy)
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentView.measure(spec, spec)
        val width = contentView.measuredWidth
        val height = contentView.measuredHeight
        var xOffset = DensityHelper.dp2px(-4f)
        var yOffset = -(anchor.height + height)
        var gravity = Gravity.START or Gravity.TOP
        if (getUsableScreenWidth(anchor.context) - xy[0] < width) {
            xOffset -= width
        }
        if (xy[1] - getStatusBarHeight() < height) {
            yOffset = 0
            gravity = Gravity.START or Gravity.BOTTOM
        }
        showAsDropDown(anchor, xOffset, yOffset, gravity)
    }
}