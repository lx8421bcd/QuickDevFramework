package com.linxiao.framework.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import kotlin.math.min

/**
 *
 *
 * Create a drawable object with text
 *
 *
 * @author linxiao
 * @since 2020-07-28
 */
class TextDrawable @JvmOverloads constructor(
    private var shape: Shape = RectShape()
) : ShapeDrawable(shape) {

    companion object {
        fun createRect(): TextDrawable {
            return TextDrawable(RectShape())
        }

        fun createRoundRect(radius: Int): TextDrawable {
            val radii = floatArrayOf(
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat()
            )
            return TextDrawable(RoundRectShape(radii, null, null))
        }

        fun createRound(): TextDrawable {
            return TextDrawable(OvalShape())
        }
    }

    private val textPaint: TextPaint = TextPaint()
    private val borderPaint: Paint
    var width = -1
    var height = -1
    var radius = 0f
    var alignment = Layout.Alignment.ALIGN_CENTER
    var ellipsize = TruncateAt.END
    var backgroundColor = Color.WHITE
    var borderSize = 0
    var borderColor = Color.TRANSPARENT
    var textSize = 0f
    var textColor = Color.BLACK
    var isBoldFont = false
    var text: String = ""

    init {
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        borderPaint = Paint()
        borderPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        paint.setColor(backgroundColor)
        borderPaint.setColor(borderColor)
        borderPaint.strokeWidth = borderSize.toFloat()
        val borderRect = RectF(getBounds())
        borderRect.inset(borderSize / 2f, borderSize / 2f)
        when (shape) {
            is OvalShape -> {
                canvas.drawOval(borderRect, borderPaint)
            }
            is RoundRectShape -> {
                canvas.drawRoundRect(borderRect, radius, radius, borderPaint)
            }
            else -> {
                canvas.drawRect(borderRect, borderPaint)
            }
        }
        textPaint.setColor(textColor)
        textPaint.isFakeBoldText = isBoldFont
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.strokeWidth = borderSize.toFloat()
        val rect = getBounds()
        width = if (width > 0) width else rect.width()
        height = if (height > 0) height else rect.height()
        textSize = if (textSize > 0) textSize else min(width, height) / 2.0f
        textPaint.textSize = textSize
        val staticLayout: StaticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(alignment)
            .setLineSpacing(0.0f, 1.0f)
            .setIncludePad(true)
            .setEllipsize(ellipsize)
            .setMaxLines((height / (textSize + textSize * 0.171)).toInt())
            .build()
        canvas.save()
        var dy = rect.top.toFloat()
        if (alignment == Layout.Alignment.ALIGN_CENTER) {
            dy = rect.top + (height - staticLayout.height) / 2f
        }
        canvas.translate(rect.left.toFloat(), dy)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        textPaint.setAlpha(alpha)
    }

    override fun setColorFilter(cf: ColorFilter?) {
        textPaint.setColorFilter(cf)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun setShape(s: Shape) {
        super.setShape(s)
        shape = s
    }
}
