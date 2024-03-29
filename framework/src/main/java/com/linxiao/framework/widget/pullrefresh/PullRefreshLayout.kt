package com.linxiao.framework.widget.pullrefresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 自定义下拉刷新布局
 *
 *
 * 下拉刷新头部使用[RefreshView] 铺成 MATCH_PARENT 的布局，
 * 各种下拉刷新效果直接继承 RefreshView 并在其中实现即可
 *
 *
 * @author lx8421bcd
 * @since 2017-06-21
 */
class PullRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {
    interface OnRefreshListener {
        fun onRefresh()
    }

    interface OnInterceptTouchEventListener {
        fun onInterceptTouchEvent(ev: MotionEvent?): Boolean
    }

    companion object {
        private val TAG = PullRefreshLayout::class.java.getSimpleName()
        private const val INVALID_POINTER = -1

        // 最大下拉距离 单位dp
        private const val DEFAULT_MAX_DRAG_DISTANCE = 80

        // 滑动阻尼
        private const val DRAG_RATE = .5f
    }

    // 嵌套滑动辅助
    private var totalUnconsumed = 0f
    private var nestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var nestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private val parentScrollConsumed = IntArray(2)
    private val parentOffsetInWindow = IntArray(2)
    private var nestedScrollInProgress = false
    private var dragging = false

    // 下拉刷新动画View
    var refreshView: RefreshView = DefaultRefreshView(context)
        set(value) {
            removeView(field)
            field = value
            field.visibility = GONE
            addView(field, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            totalDragDistance = field.maxDragDistance.toFloat()
            spinnerFinalOffset = totalDragDistance
        }
    // 下拉刷新子容器
    private var targetView: View? = null

    // 插值器，用于回弹动画
    private var decelerateInterpolator: Interpolator = DecelerateInterpolator(2f)

    // 滑动判断距离
    private var preX = 0f
    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private var spinnerFinalOffset = 0f
    // 滑动总距离
    private var totalDragDistance = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MAX_DRAG_DISTANCE.toFloat(),
        context.resources.displayMetrics
    )
    // 初始Y轴坐标
    private var initialMotionY = 0f

    // 滑动距离百分比
    private var dragPercent = 0f

    // 回弹时间
    private var durationToStartPosition = resources.getInteger(android.R.integer.config_longAnimTime)
    private var durationToCorrectPosition = resources.getInteger(android.R.integer.config_longAnimTime)

    // 初始偏移量
    private var initialOffsetTop = 0f

    // 当前距离顶部偏移量
    private var currentTranslationY = 0f

    // 动画缓存值
    private var from = 0f

    // 触发下拉的触摸点Id
    private var activePointerId = 0

    // 是否正在刷新
    var refreshing = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            captureTargetView()
            if (field) {
                moveToRefreshPosition()
            } else {
                moveToStartPosition()
            }
        }

    // 是否正在拉动
    private var draggingHeader = false

    // 是否向下层容器传递触控事件
    private var dispatchTouchDown = false

    // 默认下拉刷新监听器
    private var onRefreshListener: OnRefreshListener? = null

    val currentMoveDistance: Float
        get() = targetView?.translationY ?: 0f

    // 事件拦截监听器
    private var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null
    private val moveToStartAnimation: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop = from - from * interpolatedTime
            val offset: Float = targetTop - currentMoveDistance
            moveRefreshHeader(offset, false)
        }
    }
    private val moveToRefreshAnimation: Animation = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val endTarget = spinnerFinalOffset
            val targetTop = from + (endTarget - from) * interpolatedTime
            val offset: Float = targetTop - currentMoveDistance
            moveRefreshHeader(offset, false)
        }
    }
    private val refreshListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                refreshView.visibility = VISIBLE
                if (refreshing) {
                    refreshView.startRefreshAnim()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (!refreshing) {
                    refreshView.stopRefreshAnim()
                    moveToStartPosition()
                }
                currentTranslationY = currentMoveDistance
            }
        }
    private val toStartListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                refreshView.stopRefreshAnim()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                currentTranslationY = currentMoveDistance
            }
        }

    init {
        spinnerFinalOffset = totalDragDistance
        setWillNotDraw(false)
        isChildrenDrawingOrderEnabled = true
        refreshView = DefaultRefreshView(context)
        isNestedScrollingEnabled = true
    }

    /* ------------- implements from NestedScrollingParent ------------- */
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return isEnabled && !refreshing && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        totalUnconsumed = 0f
        nestedScrollInProgress = true
    }

    override fun onStopNestedScroll(child: View) {
        nestedScrollingParentHelper.onStopNestedScroll(child)
        nestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (totalUnconsumed > 0) {
//            if (draggingHeader) {
//                finishSpinner(mTotalUnconsumed);
//            }
            totalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            parentOffsetInWindow
        )
        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        val dy = dyUnconsumed + parentOffsetInWindow[1]
        if (dy < 0 && !canChildScrollUp()) {
            totalUnconsumed += abs(dy)
//            moveSpinner(mTotalUnconsumed); // 嵌套滚动时作下拉刷新等操作处理
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy > 0 && totalUnconsumed > 0) {
            if (dy > totalUnconsumed) {
                consumed[1] = dy - totalUnconsumed.toInt()
                totalUnconsumed = 0f
            } else {
                totalUnconsumed -= dy.toFloat()
                consumed[1] = dy
            }
//            moveSpinner((int) mTotalUnconsumed); // 嵌套滚动时作下拉刷新等操作处理
        }
        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
//        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
//                && Math.abs(dy - consumed[1]) > 0) {
//            mCircleView.setVisibility(View.GONE);
//        }
        // Now let our nested parent consume the leftovers
        val parentConsumed = parentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    /* ------------- implements from NestedScrollingChild ------------- */
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return nestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return nestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return nestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return nestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(
            dx, dy, consumed, offsetInWindow
        )
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    /* ------------------------------------------------------------- */
    private fun captureTargetView() {
        if (targetView != null || childCount <= 0) {
            return
        }
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child != refreshView) {
                targetView = child
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        captureTargetView()
        val refreshWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredWidth - getPaddingRight() - getPaddingLeft(),
            MeasureSpec.EXACTLY
        )
        val refreshHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom,
            MeasureSpec.EXACTLY
        )
        refreshView.measure(refreshWidthMeasureSpec, refreshHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        captureTargetView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        refreshing = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || canChildScrollUp() && !refreshing /*|| (dragging && mNestedScrollInProgress)*/) {
            return false
        }
        if (onInterceptTouchEventListener?.onInterceptTouchEvent(ev) == false) {
            return false
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                preX = ev.x
                dragging = true
                if (!refreshing) {
                    moveRefreshHeader(0f, true)
                }
                activePointerId = ev.getPointerId(0)
                draggingHeader = false
                val initialMotionY = getMotionY(ev, activePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                this.initialMotionY = initialMotionY
                initialOffsetTop = currentTranslationY
                dispatchTouchDown = false
                dragPercent = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                val xDiff = abs((ev.x - preX).toDouble()).toFloat()
                if (xDiff > touchSlop) {
                    return false
                }
                if (activePointerId == INVALID_POINTER) {
                    return false
                }
                val y = getMotionY(ev, activePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - initialMotionY
                if (refreshing) {
                    draggingHeader = !(yDiff < 0 && currentTranslationY <= 0)
                } else if (yDiff > touchSlop) {
                    draggingHeader = true
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                dragging = false
                draggingHeader = false
                activePointerId = INVALID_POINTER
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }
        return draggingHeader
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!draggingHeader) {
            return super.onTouchEvent(event)
        }
        if (!isEnabled || canChildScrollUp() && !refreshing) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val y = event.y
                val yDiff = y - initialMotionY
                var targetY: Float
                if (refreshing) {
                    targetY = initialOffsetTop + yDiff
                    if (canChildScrollUp()) {
                        targetY = -1f
                        initialMotionY = y
                        initialOffsetTop = 0f
                    }
                    if (targetY < 0) {
                        var obtain = event
                        if (!dispatchTouchDown) {
                            obtain = MotionEvent.obtain(event)
                            obtain.action = MotionEvent.ACTION_DOWN
                            dispatchTouchDown = true
                        }
                        targetView!!.dispatchTouchEvent(obtain)
                    } else if (targetY > totalDragDistance) {
                        targetY = totalDragDistance
                    } else {
                        if (dispatchTouchDown) {
                            val obtain = MotionEvent.obtain(event)
                            obtain.action = MotionEvent.ACTION_CANCEL
                            dispatchTouchDown = false
                            targetView!!.dispatchTouchEvent(obtain)
                        }
                    }
                } else {
                    val scrollTop = yDiff * DRAG_RATE //* DRAG_RATE
                    val originalDragPercent = scrollTop / totalDragDistance
                    if (originalDragPercent < 0) {
                        return false
                    }
                    dragPercent = min(1.0, abs(originalDragPercent.toDouble()))
                        .toFloat()
                    val extraOS = (abs(scrollTop.toDouble()) - totalDragDistance).toFloat()
                    val slingshotDist = spinnerFinalOffset
                    val tensionSlingshotPercent = max(0.0, (min(extraOS.toDouble(), (slingshotDist * 2).toDouble()) / slingshotDist)).toFloat()
                    val tensionPercent: Float = (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).pow(2.0f)) * 2f
                    val extraMove = slingshotDist * tensionPercent
                    targetY = (slingshotDist * dragPercent + extraMove).toInt().toFloat()
                    if (refreshView.visibility != VISIBLE) {
                        refreshView.visibility = VISIBLE
                    }
                }
                moveRefreshHeader(targetY - currentTranslationY, true)
                if (!refreshing) {
                    requestDisallowInterceptTouchEvent(true)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.actionIndex
                activePointerId = event.getPointerId(index)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }
                if (refreshing) {
                    if (dispatchTouchDown) {
                        targetView?.dispatchTouchEvent(event)
                        dispatchTouchDown = false
                    }
                    return false
                }
                val pointerIdx = event.findPointerIndex(activePointerId)
                val yPos = event.getY(pointerIdx)
                val overScrollTop = (yPos - initialMotionY) * DRAG_RATE
                draggingHeader = false
                finishSpinner(overScrollTop)
                activePointerId = INVALID_POINTER
                return false
            }
        }
        return true
    }

    private fun moveToStartPosition() {
        postDelayed({
            from = currentTranslationY
            moveToStartAnimation.reset()
            moveToStartAnimation.setDuration(durationToStartPosition.toLong())
            moveToStartAnimation.interpolator = decelerateInterpolator
            moveToStartAnimation.setAnimationListener(toStartListener)
            refreshView.clearAnimation()
            refreshView.startAnimation(moveToStartAnimation)
        }, 50)
    }

    private fun moveToRefreshPosition() {
        postDelayed({
            from = currentTranslationY
            moveToRefreshAnimation.reset()
            moveToRefreshAnimation.setDuration(durationToCorrectPosition.toLong())
            moveToRefreshAnimation.interpolator = decelerateInterpolator
            moveToRefreshAnimation.setAnimationListener(refreshListener)
            refreshView.clearAnimation()
            refreshView.startAnimation(moveToRefreshAnimation)
        }, 50)
    }

    private fun canChildScrollUp(): Boolean {
        return targetView?.canScrollVertically(-1) ?: false
    }

    private fun getMotionY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        return if (index < 0) -1f else ev.getY(index)
    }

    private fun finishSpinner(yDiff: Float) {
        if (yDiff > totalDragDistance) {
            refreshing = true
            onRefreshListener?.onRefresh()
        } else {
            refreshing = false
            moveToStartPosition()
        }
    }

    private fun moveRefreshHeader(offset: Float, requiresUpdate: Boolean) {
        targetView!!.translationY = currentTranslationY + offset
        currentTranslationY = currentMoveDistance
        if (requiresUpdate) {
            invalidate()
        }
        refreshView.setDragOffset(offset)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            activePointerId = ev.getPointerId(newPointerIndex)
        }
    }
}
