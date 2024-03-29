package com.linxiao.framework.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.linxiao.framework.R
import kotlin.math.ceil
import kotlin.math.max

/**
 * 小红点控件
 *
 * 在没有字符时,默认为使用系统红色,大小为8dp * 8dp的小红点,
 * 在向其设置字符时,如果字符长度>5之后的内容将会被省略,如果设置数字,当数字大于99时,默认将显示99+,
 * 可以通过setTargetView()方法,在java代码中绑定至指定View
 *
 *
 * 自定义属性：<br></br>
 * badge_color: 设置红点颜色 <br></br>
 * badge_default_size: 红点在无文字显示时的默认大小 <br></br>
 * badge_hideOnZero: 在显示数字为0时是否隐藏 <br></br>
 * badge_ellipsisDigit: 设置超限位数，超过位数后将显示超限省略符号，默认两位 <br></br>
 * badge_numberEllipsis: 在数字超限时的省略符号，默认显示99+ <br></br>
 * badge_strokeWidth: 小圆点边框宽度
 * badge_strokeColor: 小圆点边框颜色
 *
 *
 * @since 2015-11-03
 * @author linxiao
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
class BadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = BadgeView::class.java.getSimpleName()
        private var defaultTextViewTextColor = 0
    }

    private var minPaddingHorizontal = dip2Px(4f)
    private var minPaddingVertical = dip2Px(0.5f)
    private var badgeColor = Color.RED
    private var radius = 0f
    private var defaultSize = dip2Px(8f)
        set(value) {
            field = value
            requestLayout()
        }
    private var hideOnZero = false
        set(value) {
            field = value
            text = getText()
        }

    //省略标识
    private var ellipsis: String = "99+"
    private var ellipsisDigit = 2

    //补充内边距值，内容为1字符时的内边距值，
    private var extraPaddingHorizontal = 0
    private var extraPaddingVertical = 0

    //是否绑定到目标，防止重复添加
    private var badgeContainer: FrameLayout? = null

    //缓存padding
    private var cachePaddingLeft = 0
    private var cachePaddingTop = 0
    private var cachePaddingRight = 0
    private var cachePaddingBottom = 0

    // 边框参数
    private var strokeWidth = 0
    private var strokeColor = 0

    init {
        init(context, attrs)
        if (attrs == null) {
            setLayoutParams(
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            textSize = 12f
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)
            badgeColor = typedArray.getColor(R.styleable.BadgeView_badge_color, badgeColor)
            defaultSize = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badge_defaultSize, dip2Px(8f))
            hideOnZero = typedArray.getBoolean(R.styleable.BadgeView_badge_hideOnZero, false)
            ellipsis = typedArray.getString(R.styleable.BadgeView_badge_numberEllipsis) ?: ellipsis
            ellipsisDigit = typedArray.getInt(R.styleable.BadgeView_badge_ellipsisDigit, 2)
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badge_strokeWidth, 0)
            strokeColor = typedArray.getColor(R.styleable.BadgeView_badge_strokeColor, 0)
            typedArray.recycle()
        }
        // 没有自定义省略符号，使用默认的数字省略符号
        if (TextUtils.isEmpty(ellipsis)) {
            countEllipsisString()
        }
        // 在自定义属性初始化后重新setText
        if (!TextUtils.isEmpty(getText())) {
            text = getText()
        }
        if (defaultTextViewTextColor == 0) {
            defaultTextViewTextColor =
                ContextCompat.getColor(getContext(), android.R.color.secondary_text_dark)
        }
        if (currentTextColor == defaultTextViewTextColor) {
            // 如果getCurrentTextColor颜色为TextView默认值说明用户没有手动设置过颜色，使用白色默认
            setTextColor(Color.WHITE)
        }
        setGravity(Gravity.CENTER)
        execSetPadding()
    }

    private fun countEllipsisString() {
        ellipsis = ""
        for (i in 0 until ellipsisDigit) {
            ellipsis += "9"
        }
        ellipsis += "+"
    }

    /**
     * 设置两位数最小内边距
     */
    fun setMinPaddingOverOneDigit(horizontal: Int, vertical: Int) {
        minPaddingHorizontal = horizontal
        minPaddingVertical = vertical
        requestLayout()
    }

    /**
     * 设置数字
     *
     * 仅为代理setText方法将数字toString，防止使用setText设置数字时误被当做资源ID引起崩溃
     */
    fun setNumber(i: Int) {
        text = i.toString()
    }

    /**
     * 设置消息, 数字大于99时显示"99+",字符串长度大于5的部分省略
     */
    override fun setText(text: CharSequence, type: BufferType) {
        var showText = text
        if (ellipsisDigit == 0) {
            // 此时为TextView基类调用setText，子类属性还未初始化，
            // 不作任何判断直接执行基类操作
            super.setText(showText, type)
            return
        }
        if (showText.toString().matches("^\\d+$".toRegex())) {
            val number = showText.toString().toInt()
            if (number == 0 && hideOnZero) {
                hide()
            } else {
                show()
            }
            if (showText.length > ellipsisDigit) {
                showText = ellipsis
            }
        } else if (showText.length > 5) {
            showText = showText.subSequence(0, 4).toString() + "..."
        }
        super.setText(showText, type)
        execSetPadding()
    }

    fun setBadgeStroke(width: Int, color: Int) {
        strokeWidth = width
        strokeColor = color
        setBadgeBackground()
    }

    /**
     * 设置红点背景,在字符数为1时显示原型,在字符数超过1时显示圆角矩形
     */
    private fun setBadgeBackground() {
        val defaultBgDrawable = GradientDrawable()
        defaultBgDrawable.setCornerRadius(radius)
        defaultBgDrawable.setColor(badgeColor)
        if (strokeWidth != 0 && strokeColor != 0) {
            defaultBgDrawable.setStroke(strokeWidth, strokeColor)
        }
        super.setBackground(defaultBgDrawable)
    }

    fun show() {
        this.visibility = VISIBLE
    }

    fun hide() {
        this.visibility = GONE
    }

    /**
     * 解除小红点对某一View的绑定
     *
     * 此操作将会清除[.setTargetView]方法在目标View外套的FrameLayout，
     * 还原目标View原本的状态
     */
    fun unbindTargetView() {
        badgeContainer?.apply {
            if (childCount <= 0) {
                return
            }
            val lastTarget = this.getChildAt(0)
            if (lastTarget != null) {
                val lastParent = this.parent as ViewGroup
                val lastLayoutParams = this.layoutParams
                this.removeView(lastTarget)
                lastParent.removeView(this)
                lastTarget.setLayoutParams(lastLayoutParams)
                lastParent.addView(lastTarget)
            }
            removeAllViews()
        }
        badgeContainer = null
    }

    /**
     * 将红点绑定到某个现有控件上
     *
     * @param target       目标控件
     * @param badgeGravity 红点相对目标控件位置
     * @param marginLeft   左外边距
     * @param marginTop    上外边距
     * @param marginRight  右外边距
     * @param marginBottom 下外边距
     */
    @JvmOverloads
    fun setTargetView(
        target: View?,
        badgeGravity: Int = Gravity.END,
        marginLeft: Int = 0,
        marginRight: Int = 0,
        marginTop: Int = 0,
        marginBottom: Int = 0,
    ) {
        target ?: return
        parent?.apply {
            (this as ViewGroup).removeView(this)
        }
        if (target.parent is ViewGroup) {
            val parentContainer = target.parent as ViewGroup
            if (parentContainer == badgeContainer) {
                //对同一个目标执行setTargetView;
                val badgeLayoutParam = this.layoutParams as FrameLayout.LayoutParams
                badgeLayoutParam.gravity = badgeGravity
                badgeLayoutParam.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                return
            }
            unbindTargetView()
            val groupIndex = parentContainer.indexOfChild(target)
            parentContainer.removeView(target)
            badgeContainer = FrameLayout(context)
            val parentLayoutParams = target.layoutParams
            badgeContainer!!.setLayoutParams(parentLayoutParams)
            target.setLayoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams)
            badgeContainer!!.addView(target)
            badgeContainer!!.addView(this)
            val badgeLayoutParam = this.layoutParams as FrameLayout.LayoutParams
            badgeLayoutParam.gravity = badgeGravity
            badgeLayoutParam.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        } else if (target.parent == null) {
            Log.e(javaClass.getSimpleName(), "ParentView is needed")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textLength = getText().length
        if (textLength <= 0) {
            setMeasuredDimension(defaultSize, defaultSize)
            return
        }
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
//        if (mode != MeasureSpec.EXACTLY) {
//            execSetPadding();
//        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        radius = (bottom - top).toFloat()
        setBadgeBackground()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        cachePaddingLeft = left
        cachePaddingTop = top
        cachePaddingRight = right
        cachePaddingBottom = bottom
        execSetPadding()
    }

    private fun execSetPadding() {
        if (ellipsisDigit == 0) {
            // 此时为TextView基类调用setText，子类属性还未初始化，
            // 不作任何判断直接执行基类操作
            super.setPadding(
                cachePaddingLeft,
                cachePaddingTop,
                cachePaddingRight,
                cachePaddingBottom
            )
            return
        }
        var textLength = 0
        if (getText() != null) {
            textLength = getText().length
        }
        if (textLength == 0) {
            super.setPadding(
                cachePaddingLeft,
                cachePaddingTop,
                cachePaddingRight,
                cachePaddingBottom
            )
            return
        }
        if (textLength == 1) {
            var padding = max(
                max(cachePaddingLeft.toDouble(), cachePaddingRight.toDouble()),
                max(cachePaddingTop.toDouble(), cachePaddingBottom.toDouble())
            ).toInt()
            padding += minPaddingVertical
            // 在为单个字符时, 根据文字宽高计算出的水平/垂直方向补充padding, 使得控件为正方形
            calculateExtraPadding()
            super.setPadding(
                padding + extraPaddingHorizontal,
                padding + extraPaddingVertical,
                padding + extraPaddingHorizontal,
                padding + extraPaddingVertical
            )
            return
        }
        val paddingHorizontal = max(cachePaddingLeft.toDouble(), cachePaddingRight.toDouble()).toInt()
        val paddingVertical = max(cachePaddingTop.toDouble(), cachePaddingBottom.toDouble()).toInt()
        super.setPadding(
            minPaddingHorizontal + paddingHorizontal,
            minPaddingVertical + paddingVertical,
            minPaddingHorizontal + paddingHorizontal,
            minPaddingVertical + paddingVertical
        )
    }

    override fun getPaddingLeft(): Int {
        if (super.getPaddingLeft() == 0) {
            return 0
        }
        val textLength = getText().length
        if (textLength == 0) {
            return 0
        }
        if (textLength == 1) {
            return super.getPaddingLeft() - extraPaddingHorizontal
        }
        return if (textLength > 1) {
            super.getPaddingLeft() - minPaddingHorizontal
        } else super.getPaddingLeft()
    }

    override fun getPaddingRight(): Int {
        if (super.getPaddingRight() == 0) {
            return 0
        }
        val textLength = getText().length
        if (textLength == 0) {
            return 0
        }
        if (textLength == 1) {
            return super.getPaddingRight() - extraPaddingHorizontal
        }
        return if (textLength > 1) {
            super.getPaddingRight() - minPaddingHorizontal
        } else super.getPaddingRight()
    }

    override fun getPaddingTop(): Int {
        if (super.getPaddingTop() == 0) {
            return 0
        }
        val textLength = getText().length
        if (textLength == 0) {
            return 0
        }
        if (textLength == 1) {
            return super.getPaddingTop() - extraPaddingVertical
        }
        return if (textLength > 1) {
            super.getPaddingTop() - minPaddingVertical
        } else super.getPaddingTop()
    }

    override fun getPaddingBottom(): Int {
        if (super.getPaddingBottom() == 0) {
            return 0
        }
        val textLength = getText().length
        if (textLength == 0) {
            return 0
        }
        if (textLength == 1) {
            return super.getPaddingBottom() - extraPaddingVertical
        }
        return if (textLength > 1)
            super.getPaddingBottom() - minPaddingVertical
         else
            super.getPaddingBottom()
    }

    private fun calculateExtraPadding() {
        if (getText().length != 1) {
            extraPaddingHorizontal = 0
            extraPaddingVertical = 0
            return
        }
        // 此处根据文字宽高计算，至少有一个补充值为0
        val textWidth = paint.measureText(getText().toString()).toInt()
        val fm = paint.getFontMetrics()
        val textHeight = (ceil((fm.descent - fm.top).toDouble()) + 2).toInt()
        if (textWidth > textHeight) {
            extraPaddingHorizontal = 0
            extraPaddingVertical = (textWidth - textHeight) / 2
        } else if (textHeight > textWidth) {
            extraPaddingHorizontal = (textHeight - textWidth) / 2
            extraPaddingVertical = 0
        } else {
            extraPaddingHorizontal = 0
            extraPaddingVertical = 0
        }
    }

    override fun setBackground(background: Drawable) {
        setBadgeBackground()
    }

    override fun setBackgroundColor(color: Int) {
        badgeColor = color
        setBadgeBackground()
    }

    override fun setBackgroundDrawable(background: Drawable) {
        setBadgeBackground()
    }

    override fun setBackgroundResource(resId: Int) {
        setBadgeBackground()
    }

    private fun dip2Px(dip: Float): Int {
        return (dip * resources.displayMetrics.density + 0.5f).toInt()
    }
}