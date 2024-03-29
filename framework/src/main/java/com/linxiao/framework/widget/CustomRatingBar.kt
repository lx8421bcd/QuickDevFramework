package com.linxiao.framework.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.linxiao.framework.R

/**
 * 自定义评星控件
 *
 * @author linxiao
 * @version 1.0
 * @since 2018-06-07
 */
class CustomRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    interface OnRatingChangeListener {
        fun onRatingChange(rating: Double)
    }

    companion object {
        private val TAG = CustomRatingBar::class.java.getSimpleName()
        private fun drawableToBitmap(drawable: Drawable?): Bitmap {
            val w = drawable!!.intrinsicWidth
            val h = drawable.intrinsicHeight
            val config =
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            val bitmap = Bitmap.createBitmap(w, h, config)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            drawable.draw(canvas)
            return bitmap
        }
    }

    private var clickable = false
    private var showHalfStar = false
    private var showPercent = false
    private var starCount = 0
    private var starWidth = dip2px(24f)
    private var starHeight = dip2px(24f)
    private var starPaddingBoth = 0
    private var starEmptyDrawable: Drawable? = null
    private var starFillDrawable: Drawable? = null
    private var starHalfDrawable: Drawable? = null
    private var onRatingChangeListener: OnRatingChangeListener? = null
    var rating = 0.0
        set(value) {
            var rating = value
            rating = (rating * 10).toInt() * 1.0 / 10
            for (i in 0 until starCount) {
                val ivStar = getChildAt(i) as ImageView
                val starIndex = (i + 1).toDouble()
                if (starIndex - rating <= 0) {
                    ivStar.setImageDrawable(starFillDrawable)
                } else if (starIndex - rating < 1) {
                    ivStar.setImageDrawable(getLastStarDrawable(rating))
                } else {
                    ivStar.setImageDrawable(starEmptyDrawable)
                }
            }
            field = rating
        }
    private val onStarClick = OnClickListener { v ->
        var index = -1
        for (i in 0 until childCount) {
            if (getChildAt(i) === v) {
                index = i
            }
        }
        if (index < 0) {
            return@OnClickListener
        }
        val rating = index + 1
        if (rating != this@CustomRatingBar.rating.toInt()) {
            this@CustomRatingBar.rating = rating.toDouble()
            onRatingChangeListener?.onRatingChange(this@CustomRatingBar.rating)
        }
    }

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        if (attrs != null) {
            val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar)
            starEmptyDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_emptyImage)
            starHalfDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_halfImage)
            starFillDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_filledImage)
            starWidth = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starWidth, dip2px(24f))
            starHeight = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starHeight, dip2px(24f))
            starPaddingBoth = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starPadding, dip2px(2f))
            starCount = mTypedArray.getInteger(R.styleable.CustomRatingBar_starCount, 5)
            rating = mTypedArray.getFloat(R.styleable.CustomRatingBar_rating, 0f).toDouble()
            clickable = mTypedArray.getBoolean(R.styleable.CustomRatingBar_starClickable, false)
            showHalfStar = mTypedArray.getBoolean(R.styleable.CustomRatingBar_showHalf, false)
            showPercent = mTypedArray.getBoolean(R.styleable.CustomRatingBar_showPercent, true)
            mTypedArray.recycle()
        }
        if (starEmptyDrawable == null) {
            starEmptyDrawable = ContextCompat.getDrawable(context, R.drawable.ic_rating_empty)
        }
        if (starFillDrawable == null) {
            starFillDrawable = ContextCompat.getDrawable(context, R.drawable.ic_rating_filled)
        }
        initStarImageViews()
    }

    private fun initStarImageViews() {
        removeAllViews()
        for (i in 0 until starCount) {
            val imageView = ImageView(context)
            val param = LayoutParams(starWidth, starHeight, 1.0f)
            imageView.setLayoutParams(param)
            imageView.setPadding(starPaddingBoth, 0, starPaddingBoth, 0)
            imageView.setOnClickListener(onStarClick)
            imageView.setEnabled(clickable)
            imageView.setImageDrawable(starEmptyDrawable)
            addView(imageView)
        }
        this.rating = rating
    }

    private fun getLastStarDrawable(rating: Double): Drawable? {
        // decimal part of rating, used to decide show half star or not
        val decimalPart = rating - rating.toInt()

        // neither show percentage star image nor half style image, treat decimal part as 0
        if (!showPercent && !showHalfStar) {
            return starEmptyDrawable
        }
        // if only enabled show half style image, treat decimal part lower than 0.7 as half star
        if (!showPercent && starHalfDrawable != null) {
            return if (decimalPart > 0.7) {
                starFillDrawable
            } else {
                starHalfDrawable
            }
        }
        /*
         if both show percentage and show half style is enabled,
         use half style image as background layer first if it is not null
         */
        val backLayer: Bitmap = if (starHalfDrawable != null) {
            drawableToBitmap(starHalfDrawable)
        } else {
            drawableToBitmap(starEmptyDrawable)
        }
        var frontLayer = drawableToBitmap(starFillDrawable)
        val frontWidth = (frontLayer.getWidth() * decimalPart).toInt()
        frontLayer = Bitmap.createBitmap(frontLayer, 0, 0, frontWidth, frontLayer.getHeight())
        val bitmap = backLayer.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val baseRect = Rect(0, 0, backLayer.getWidth(), backLayer.getHeight())
        val frontRect = Rect(0, 0, backLayer.getHeight(), frontLayer.getHeight())
        canvas.drawBitmap(frontLayer, frontRect, baseRect, null)
        return BitmapDrawable(bitmap)
    }

    fun setStarHalfDrawable(starHalfDrawable: Drawable?) {
        this.starHalfDrawable = starHalfDrawable
    }

    fun setOnRatingChangeListener(onRatingChangeListener: OnRatingChangeListener?) {
        this.onRatingChangeListener = onRatingChangeListener
    }

    fun setStarClickable(clickable: Boolean) {
        this.clickable = clickable
        for (i in 0 until childCount) {
            getChildAt(i).setEnabled(clickable)
        }
    }

    fun showHalfStar(show: Boolean) {
        showHalfStar = show
        rating = rating
    }

    fun setShowPercent(showPercent: Boolean) {
        this.showPercent = showPercent
        rating = rating
    }

    fun setStarImage(empty: Drawable?, half: Drawable?, filled: Drawable?) {
        if (empty != null) {
            starEmptyDrawable = empty
        }
        if (half != null) {
            starHalfDrawable = half
        }
        if (filled != null) {
            starFillDrawable = filled
        }
        rating = rating
    }

    fun setStarImage(empty: Drawable?, filled: Drawable?) {
        if (empty != null) {
            starEmptyDrawable = empty
        }
        if (filled != null) {
            starFillDrawable = filled
        }
        rating = rating
    }

    fun setStarWidth(starWidth: Int) {
        if (this.starWidth == starWidth) {
            return
        }
        this.starWidth = starWidth
        for (i in 0 until childCount) {
            val ivStar = getChildAt(i) as ImageView
            ivStar.layoutParams.width = starWidth
        }
        requestLayout()
    }

    fun setStarHeight(starHeight: Int) {
        if (this.starHeight == starHeight) {
            return
        }
        this.starHeight = starHeight
        for (i in 0 until childCount) {
            val ivStar = getChildAt(i) as ImageView
            ivStar.layoutParams.height = starHeight
        }
        requestLayout()
    }

    fun setStarCount(starCount: Int) {
        if (this.starCount == starCount) {
            return
        }
        this.starCount = starCount
        initStarImageViews()
    }

    fun setStarPadding(padding: Int) {
        starPaddingBoth = padding
        for (i in 0 until childCount) {
            val ivStar = getChildAt(i) as ImageView
            ivStar.setPadding(starPaddingBoth, 0, starPaddingBoth, 0)
        }
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun px2dip(pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}
