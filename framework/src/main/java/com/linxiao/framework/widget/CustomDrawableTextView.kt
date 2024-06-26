package com.linxiao.framework.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import com.linxiao.framework.R

/**
 * 自定义Drawable大小TextView
 *
 * 通过t_drawableWidth和t_drawableHeight两个属性设定drawable大小，
 * 也可以通过各个方向的宽高属性设定各个drawable的大小，
 * 如果不设定,drawable默认尺寸为0
 *
 * @author linxiao
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
class CustomDrawableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private var drawableWidth = 0
    private var drawableHeight = 0
    private var drawableLeftWidth = 0
    private var drawableLeftHeight = 0
    private var drawableRightWidth = 0
    private var drawableRightHeight = 0
    private var drawableTopWidth = 0
    private var drawableTopHeight = 0
    private var drawableBottomWidth = 0
    private var drawableBottomHeight = 0

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attr: AttributeSet?) {
        if (attr != null) {
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.CustomDrawableTextView)
            drawableWidth = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableWidth,
                0
            )
            drawableHeight = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableHeight,
                0
            )
            drawableLeftWidth = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableLeftWidth,
                0
            )
            drawableLeftHeight = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableLeftHeight,
                0
            )
            drawableRightWidth = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableRightWidth,
                0
            )
            drawableRightHeight = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableRightHeight,
                0
            )
            drawableTopWidth = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableTopWidth,
                0
            )
            drawableTopHeight = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableTopHeight,
                0
            )
            drawableBottomWidth = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableBottomWidth,
                0
            )
            drawableBottomHeight = typedArray.getDimensionPixelOffset(
                R.styleable.CustomDrawableTextView_t_drawableBottomHeight,
                0
            )
            typedArray.recycle()
        }
        val drawables = getCompoundDrawables()
        val left = drawables[0]
        val top = drawables[1]
        val right = drawables[2]
        val bottom = drawables[3]
        setCompoundDrawables(left, top, right, bottom)
    }

    private fun setDrawableBounds(
        drawable: Drawable?,
        setWidth: Int,
        setHeight: Int,
        defWidth: Int,
        defHeight: Int
    ) {
        val width: Int = if (setWidth > 0) setWidth else defWidth
        val height: Int = if (setHeight > 0) setHeight else defHeight
        drawable?.bounds = Rect(0, 0, width, height)
    }

    fun setDrawableLeft(drawable: Drawable?) {
        val drawables = getCompoundDrawables()
        val top = drawables[1]
        val right = drawables[2]
        val bottom = drawables[3]
        setCompoundDrawables(drawable, top, right, bottom)
    }

    fun setDrawableRight(drawable: Drawable?) {
        val drawables = getCompoundDrawables()
        val left = drawables[0]
        val top = drawables[1]
        val bottom = drawables[3]
        setCompoundDrawables(left, top, drawable, bottom)
    }

    fun setDrawableTop(drawable: Drawable?) {
        val drawables = getCompoundDrawables()
        val left = drawables[0]
        val right = drawables[2]
        val bottom = drawables[3]
        setCompoundDrawables(left, drawable, right, bottom)
    }

    fun setDrawableBottom(drawable: Drawable?) {
        val drawables = getCompoundDrawables()
        val left = drawables[0]
        val top = drawables[1]
        val right = drawables[2]
        setCompoundDrawables(left, top, right, drawable)
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        setDrawableBounds(
            left,
            drawableLeftWidth,
            drawableLeftHeight,
            drawableWidth,
            drawableHeight
        )
        setDrawableBounds(
            top,
            drawableTopWidth,
            drawableTopHeight,
            drawableWidth,
            drawableHeight
        )
        setDrawableBounds(
            right,
            drawableRightWidth,
            drawableRightHeight,
            drawableWidth,
            drawableHeight
        )
        setDrawableBounds(
            bottom,
            drawableBottomWidth,
            drawableBottomHeight,
            drawableWidth,
            drawableHeight
        )
        super.setCompoundDrawables(left, top, right, bottom)
    }
}
