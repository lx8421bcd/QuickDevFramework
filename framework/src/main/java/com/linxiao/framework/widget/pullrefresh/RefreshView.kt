package com.linxiao.framework.widget.pullrefresh

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout

/**
 * 下拉刷新HeaderView
 *
 * @author lx8421bcd
 * @since  2017-06-21
 */
abstract class RefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @JvmField
    var maxDragDistance = dp2px(80)

    abstract fun setDragOffset(offset: Float)
    abstract fun startRefreshAnim()
    abstract fun stopRefreshAnim()
    abstract fun isRunningAnim(): Boolean
    protected fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
