package com.linxiao.framework.widget.pullrefresh

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar

/**
 * Material 圆圈式下拉刷新布局
 *
 * @author lx8421bcd
 * @since  2017-06-21
 */
class DefaultRefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RefreshView(context, attrs, defStyleAttr) {

    private var refreshView = ProgressBar(getContext())

    init {
        maxDragDistance = dp2px(40)
        val refreshViewSize = dp2px(28)
        val params = LayoutParams(refreshViewSize, refreshViewSize)
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.setMargins(0, -refreshViewSize, 0, 0)
        addView(refreshView, params)
        refreshView.indeterminateDrawable.setColorFilter(
            -0x10000,
            PorterDuff.Mode.MULTIPLY
        )
    }

    override fun setDragOffset(offset: Float) {
        val params = refreshView.layoutParams as LayoutParams
        params.topMargin += offset.toInt()
        requestLayout()
    }

    override fun startRefreshAnim() {
//        mRefreshView.setProgress();
    }

    override fun stopRefreshAnim() {}
    override fun isRunningAnim(): Boolean {
        return false
    }
}
