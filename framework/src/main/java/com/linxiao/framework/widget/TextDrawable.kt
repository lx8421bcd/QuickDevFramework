package com.linxiao.framework.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * <p>
 * Create a drawable object with text
 * </p>
 *
 * @author linxiao
 * @since 2020-07-28
 */
public class TextDrawable extends ShapeDrawable {

    private Shape shape;
    private TextPaint textPaint;
    private Paint borderPaint;

    private int width = -1;
    private int height = -1;
    private float radius;
    private Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
    private TextUtils.TruncateAt ellipsize = TextUtils.TruncateAt.END;
    private int backgroundColor = Color.WHITE;
    private int borderSize;
    private int borderColor = Color.TRANSPARENT;
    private float textSize;
    private int textColor = Color.BLACK;
    private boolean boldFont;
    private String text;

    public static TextDrawable createRect() {
        return new TextDrawable(new RectShape());
    }

    public static TextDrawable createRoundRect(int radius) {
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        return new TextDrawable(new RoundRectShape(radii, null, null));
    }

    public static TextDrawable createRound() {
        return new TextDrawable(new OvalShape());
    }

    public TextDrawable() {
        this(new RectShape());
    }

    public TextDrawable(Shape s) {
        super(s);
        shape = s;
        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        getPaint().setColor(backgroundColor);

        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderSize);

        RectF borderRect = new RectF(getBounds());
        borderRect.inset(borderSize / 2f, borderSize / 2f);

        if (shape instanceof OvalShape) {
            canvas.drawOval(borderRect, borderPaint);
        } else if (shape instanceof RoundRectShape) {
            canvas.drawRoundRect(borderRect, radius, radius, borderPaint);
        } else {
            canvas.drawRect(borderRect, borderPaint);
        }

        textPaint.setColor(textColor);
        textPaint.setFakeBoldText(boldFont);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setStrokeWidth(borderSize);
        Rect rect = getBounds();
        width = width > 0 ? width : rect.width();
        height = height > 0 ? height : rect.height();
        textSize = textSize > 0 ? textSize : Math.min(width, height) / 2.0f;
        textPaint.setTextSize(textSize);
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, width)
                    .setAlignment(alignment)
                    .setLineSpacing(0.0f, 1.0f)
                    .setIncludePad(true)
                    .setEllipsize(ellipsize)
                    .setMaxLines((int) (height / (textSize + textSize * 0.171)))
                    .build();
        } else {
            staticLayout = new StaticLayout(text, 0, text.length(), textPaint, width,
                    alignment, 1.0F, 0.0F,
                    true, ellipsize, Integer.MAX_VALUE);
        }
        canvas.save();
        float dy = rect.top;
        if (alignment == Layout.Alignment.ALIGN_CENTER) {
            dy = rect.top + (height - staticLayout.getHeight()) / 2f;
        }
        canvas.translate(rect.left, dy);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public void setShape(Shape s) {
        super.setShape(s);
        this.shape = s;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Layout.Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Layout.Alignment alignment) {
        this.alignment = alignment;
    }

    public TextUtils.TruncateAt getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(TextUtils.TruncateAt ellipsize) {
        this.ellipsize = ellipsize;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isBoldFont() {
        return boldFont;
    }

    public void setBoldFont(boolean boldFont) {
        this.boldFont = boldFont;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
