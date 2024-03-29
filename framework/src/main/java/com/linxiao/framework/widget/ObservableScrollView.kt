package com.linxiao.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

/**
 * 设置滚动距离监听的ScrollView
 *
 * 通过设置OnScrollChangedListener监听滚动距离
 *
 * @author linxiao
 * @version 1.0
 */
class ObservableScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {
    interface OnScrollChangedListener {
        fun onScrollChanged(
            scrollView: NestedScrollView?,
            scrolledX: Int,
            scrolledY: Int,
            dx: Int,
            dy: Int
        )
    }

    private var listener: OnScrollChangedListener? = null

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        this.listener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val dl = l - oldl
        val dt = t - oldt
        listener?.onScrollChanged(this, scrollX, scrollY, dl, dt)
    }
}
