package com.linxiao.framework.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.view.children
import kotlin.math.max

/**
 * 高亮引导控件
 *
 *  对于单个引导页创建，请使用 [.newInstance] 创建引导页实例，
 * 如果有依次弹出若干引导页的需求，可以使用[.newGuideQueue]创建引导页队列，
 * 队列会自动管理引导页销毁逻辑
 *
 *
 * @author lx8421bcd
 * @since 2017-08-03
 */
class HighlightGuideView private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    /**
     * 引导页销毁监听
     */
    fun interface OnDismissListener {
        fun onDismiss()
    }

    companion object {
        /**
         * 方形高亮
         */
        const val STYLE_RECT = 0

        /**
         * 原型高亮
         */
        const val STYLE_CIRCLE = 1

        /**
         * 椭圆形高亮
         */
        const val STYLE_OVAL = 2

        /**
         * 没有高亮目标的引导控件容器ID
         */
        private const val NO_TARGET_GUIDE_ID = 10152

        /**
         * 新建引导页实例
         *
         * 必须使用Activity
         */
        fun newInstance(activity: Activity): HighlightGuideView {
            return HighlightGuideView(activity)
        }

        /**
         * 新建引导页队列
         */
        fun newGuideQueue(): GuideQueue {
            return GuideQueue()
        }
    }

    // 需要高亮页面的根布局
    private var rootView: ViewGroup = (context as Activity).findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
    private val targetViewMap: MutableMap<Int, View> = ArrayMap()
    private val guideViewsMap: MutableMap<Int, MutableList<View>> = ArrayMap()
    private val highlightPaddingMap: MutableMap<Int, Int> = ArrayMap()
    private val guideRelativePosMap: MutableMap<Int, Map<String, Int>> = ArrayMap()
    private var highlightStyle = STYLE_CIRCLE // 默认为圆形高亮
    // 绘制参数
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG) // 开启抗锯齿和抗抖动
    private val screenWidth = resources.displayMetrics.widthPixels
    private val screenHeight = resources.displayMetrics.heightPixels
    private val backgroundBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
    private var backgroundColor = -0x34000000 //背景默认颜色
    private val canvas = Canvas(backgroundBitmap)
    private var touchOutsideCancelable = true
    private val dismissListeners: MutableList<OnDismissListener> = ArrayList()

    init {
        setWillNotDraw(false)
        highlightPaint.setARGB(0, 255, 0, 0)
        highlightPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
        canvas.drawColor(backgroundColor)
    }

    fun setContainerView(view: ViewGroup) {
        dismiss()
        rootView = view
    }

    /**
     * 设置目标View高亮区域内边距
     * @param view 目标View
     * @param padding 内边距
     */
    fun setHighlightPadding(view: View, padding: Int): HighlightGuideView {
        highlightPaddingMap[view.hashCode()] = padding
        return this
    }

    /**
     * 添加目标引导控件
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param guideView 图片资源Drawable
     * @param width 引导图片宽度
     * @param height 引导图片高度
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     */
    @JvmOverloads
    fun addGuideView(
        targetView: View?,
        guideView: View,
        width: Int? = null,
        height: Int? = null,
        relativeX: Int = 0,
        relativeY: Int = 0,
    ): HighlightGuideView {
        val lp = LayoutParams(
            width ?: LayoutParams.WRAP_CONTENT,
            height ?: LayoutParams.WRAP_CONTENT
        )
        guideView.setLayoutParams(lp)
        val paramsMap: MutableMap<String, Int> = ArrayMap()
        paramsMap["x"] = relativeX
        paramsMap["y"] = relativeY
        guideRelativePosMap[guideView.hashCode()] = paramsMap
        guideViewsMap.getOrPut(targetView?.hashCode() ?: NO_TARGET_GUIDE_ID) {
            mutableListOf()
        }.add(guideView)
        this.addView(guideView)
        return this
    }

    /**
     * 添加引导图片
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param guideDrawable 图片资源Drawable
     * @param width 引导图片宽度
     * @param height 引导图片高度
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     */
    @JvmOverloads
    fun addGuideImage(
        targetView: View?,
        guideDrawable: Drawable?,
        width: Int? = null,
        height: Int? = null,
        relativeX: Int = 0,
        relativeY: Int = 0,
    ): HighlightGuideView {
        val guideImageView = ImageView(context)
        guideImageView.setImageDrawable(guideDrawable)
        addGuideView(targetView, guideImageView, width, height, relativeX, relativeY)
        return this
    }

    /**
     * 添加引导图片
     *
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param resId 图片资源ID
     * @param width 引导图片宽度
     * @param height 引导图片高度
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     */
    @JvmOverloads
    fun addGuideImageResource(
        targetView: View?,
        @DrawableRes resId: Int,
        width: Int? = null,
        height: Int? = null,
        relativeX: Int = 0,
        relativeY: Int = 0,
    ): HighlightGuideView {
        val guideImageView = ImageView(context)
        guideImageView.setImageResource(resId)
        addGuideView(targetView, guideImageView, width, height, relativeX, relativeY)
        return this
    }

    /**
     * 是否允许点击空白区域隐藏HighlightGuideView
     *
     * 默认为true
     *
     * @param cancel 是否允许
     */
    fun setCancelOnTouchOutside(cancel: Boolean): HighlightGuideView {
        touchOutsideCancelable = cancel
        return this
    }

    /**
     * 设置高亮样式
     *
     * @param style 高亮样式
     */
    fun setHighlightStyle(style: Int): HighlightGuideView {
        if (style > 2 || style < 0) {
            highlightStyle = 1
        }
        highlightStyle = style
        return this
    }

    /**
     * 设置蒙版背景颜色
     *
     * @param color 颜色色值
     */
    fun setMaskBackgroundColor(@ColorInt color: Int): HighlightGuideView {
        backgroundColor = color
        return this
    }

    /**
     * 设置蒙版背景颜色
     *
     * @param resId 颜色资源ID
     */
    fun setMaskBackgroundRes(@ColorRes resId: Int): HighlightGuideView {
        backgroundColor = ContextCompat.getColor(context, resId)
        return this
    }

    fun addOnDismissListener(listener: OnDismissListener): HighlightGuideView {
        dismissListeners.add(listener)
        return this
    }

    /**
     * 显示引导
     */
    fun show() {
        if (rootView.children.contains(this)) {
            return
        }
        rootView.addView(
            this,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.visibility = VISIBLE
    }

    /**
     * 移除引导
     */
    fun dismiss() {
        this.visibility = GONE
        rootView.removeView(this)
        for (listener in dismissListeners) {
            listener.onDismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }
        if (touchOutsideCancelable) {
            dismiss()
        }
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (targetId in guideViewsMap.keys) {
            var targetX = 0
            var targetY = 0
            if (targetId != NO_TARGET_GUIDE_ID) {
                val targetView = targetViewMap[targetId] ?: continue
                val rect = getTargetViewRect(targetView)
                targetX = rect.left
                targetY = rect.top
            }
            for (guideView in guideViewsMap[targetId] ?: mutableListOf()) {
                val relativeX = guideRelativePosMap[guideView.hashCode()]?.get("x") ?: 0
                val relativeY = guideRelativePosMap[guideView.hashCode()]?.get("y") ?: 0
                val params = guideView.layoutParams as LayoutParams
                //尝试兼容View中设定的水平居中或垂直居中属性
                val gravity = params.gravity
                var absoluteGravity: Int
                val layoutDirection = getLayoutDirection()
                absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
                if (absoluteGravity == Gravity.CENTER_HORIZONTAL) {
                    guideView.x = ((screenWidth - guideView.measuredWidth) / 2).toFloat()
                } else {
                    guideView.x = (targetX + params.leftMargin + relativeX).toFloat()
                }
                if (verticalGravity == Gravity.CENTER_VERTICAL) {
                    guideView.y = ((screenHeight - guideView.measuredHeight) / 2).toFloat()
                } else {
                    guideView.y = (targetY + params.topMargin + relativeY).toFloat()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制背景
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)
        //绘制高亮区域
        if (targetViewMap.isEmpty()) {
            return
        }
        for (targetId in guideViewsMap.keys) {
            val targetView = targetViewMap[targetId] ?: continue
            val padding = highlightPaddingMap[targetId] ?: 0
            drawHighlightArea(targetView, padding)
        }
    }

    /**
     * 绘制高亮区域
     */
    private fun drawHighlightArea(highlightView: View, padding: Int) {
        val width = highlightView.width.toFloat()
        val height = highlightView.height.toFloat()
        //高亮控件坐标
        val targetRect = getTargetViewRect(highlightView)
        val left = targetRect.left
        val top = targetRect.top
        val right = targetRect.right
        val bottom = targetRect.bottom
        val highlightRect: RectF
        when (highlightStyle) {
            STYLE_RECT -> {
                highlightRect = RectF(
                    (left - padding).toFloat(),
                    (top - padding).toFloat(),
                    (right + padding).toFloat(),
                    (bottom + padding).toFloat()
                )
                canvas.drawRect(highlightRect, highlightPaint)
            }

            STYLE_OVAL -> {
                highlightRect = RectF(
                    (left - padding).toFloat(),
                    (top - padding).toFloat(),
                    (right + padding).toFloat(),
                    (bottom + padding).toFloat()
                )
                canvas.drawOval(highlightRect, highlightPaint)
            }

            STYLE_CIRCLE -> {
                val radius = ((max(width.toDouble(), height.toDouble()) + padding) / 2).toFloat()
                canvas.drawCircle(left + width / 2, top + height / 2, radius, highlightPaint)
            }
        }
    }

    /**
     * 获取目标控件在Activity根布局中的坐标矩阵
     */
    private fun getTargetViewRect(targetView: View): Rect {
        val parent = rootView.getChildAt(0)
        var decorView: View? = null
        val context = targetView.context
        if (context is Activity) {
            decorView = context.window.decorView
        }
        val result = Rect()
        val tmpRect = Rect()
        var tmp = targetView
        if (targetView === parent) {
            targetView.getHitRect(result)
            return result
        }
        while (tmp !== decorView && tmp !== parent) {
            tmp.getHitRect(tmpRect)
            if (tmp.javaClass.toString() != "NoSaveStateFrameLayout") {
                result.left += tmpRect.left
                result.top += tmpRect.top
            }
            tmp = tmp.parent as View
        }
        result.right = result.left + targetView.measuredWidth
        result.bottom = result.top + targetView.measuredHeight
        return result
    }

    /**
     * 引导页队列
     *
     * 使用此类构建按添加先后顺序依次显示的引导页队列
     *
     */
    class GuideQueue {
        private val guideViewList: MutableList<HighlightGuideView> = ArrayList()
        private var showCount = 0
        fun add(guideView: HighlightGuideView?) {
            if (guideView == null) {
                return
            }
            guideView.addOnDismissListener { showNext() }
            guideViewList.add(guideView)
        }

        /**
         * 移除引导页，显示过后的页面无法移除
         */
        fun remove(guideView: HighlightGuideView) {
            if (guideViewList.indexOf(guideView) > showCount) {
                guideViewList.remove(guideView)
            }
        }

        fun show() {
            if (guideViewList.isNotEmpty()) {
                guideViewList[0].show()
            }
        }

        /**
         * 取消后续引导显示
         */
        fun cancelAll() {
            guideViewList[showCount].dismiss()
            guideViewList.clear()
        }

        private fun showNext() {
            if (++showCount >= guideViewList.size) {
                guideViewList.clear()
                showCount = 0
                return
            }
            guideViewList[showCount].show()
        }
    }
}
