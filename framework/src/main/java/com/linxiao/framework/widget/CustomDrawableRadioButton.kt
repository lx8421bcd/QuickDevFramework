package com.linxiao.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.linxiao.framework.R;

/**
 * 
 * @author linxiao
 */
@SuppressLint("AppCompatCustomView")
public class CustomDrawableRadioButton extends RadioButton {

    private int drawableWidth;
    private int drawableHeight;
    private int drawableLeftWidth;
    private int drawableLeftHeight;
    private int drawableRightWidth;
    private int drawableRightHeight;
    private int drawableTopWidth;
    private int drawableTopHeight;
    private int drawableBottomWidth;
    private int drawableBottomHeight;
    
    public CustomDrawableRadioButton(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public CustomDrawableRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public CustomDrawableRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attr) {
        if (attr != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.CustomDrawableRadioButton);
            drawableWidth = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableWidth, 0);
            drawableHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableHeight, 0);
            drawableLeftWidth = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableLeftWidth, 0);
            drawableLeftHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableLeftHeight, 0);
            drawableRightWidth = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableRightWidth, 0);
            drawableRightHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableRightHeight, 0);
            drawableTopWidth = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableTopWidth, 0);
            drawableTopHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableTopHeight, 0);
            drawableBottomWidth = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableBottomWidth, 0);
            drawableBottomHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomDrawableRadioButton_r_drawableBottomHeight, 0);

            typedArray.recycle();

        }

        Drawable drawables[] = getCompoundDrawables();
        Drawable left = drawables[0];
        Drawable top = drawables[1];
        Drawable right = drawables[2];
        Drawable bottom = drawables[3];
        setCompoundDrawables(left, top, right, bottom);

    }

    private void setDrawableBounds(Drawable drawable, int setWidth, int setHeight, int defWidth, int defHeight) {
        int width, height;
        width = setWidth > 0 ? setWidth : defWidth;
        height = setHeight > 0 ? setHeight : defHeight;
        if(drawable != null) {
            Rect drawableRect = new Rect(0, 0, width, height);
            drawable.setBounds(drawableRect);
        }
    }

    public void setDrawableLeft(Drawable drawable) {
        Drawable drawables[] = getCompoundDrawables();
        Drawable top = drawables[1];
        Drawable right = drawables[2];
        Drawable bottom = drawables[3];
        setCompoundDrawables(drawable, top, right, bottom);
    }

    public void setDrawableRight(Drawable drawable) {
        Drawable drawables[] = getCompoundDrawables();
        Drawable left = drawables[0];
        Drawable top = drawables[1];
        Drawable bottom = drawables[3];
        setCompoundDrawables(left, top, drawable, bottom);
    }

    public void setDrawableTop(Drawable drawable) {
        Drawable drawables[] = getCompoundDrawables();
        Drawable left = drawables[0];
        Drawable right = drawables[2];
        Drawable bottom = drawables[3];
        setCompoundDrawables(left, drawable, right, bottom);
    }

    public void setDrawableBottom(Drawable drawable) {
        Drawable drawables[] = getCompoundDrawables();
        Drawable left = drawables[0];
        Drawable top = drawables[1];
        Drawable right = drawables[2];
        setCompoundDrawables(left, top, right, drawable);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        setDrawableBounds(left, drawableLeftWidth, drawableLeftHeight, drawableWidth, drawableHeight);
        setDrawableBounds(top, drawableTopWidth,drawableTopHeight, drawableWidth,drawableHeight);
        setDrawableBounds(right, drawableRightWidth, drawableRightHeight,drawableWidth,drawableHeight);
        setDrawableBounds(bottom, drawableBottomWidth, drawableBottomHeight, drawableWidth,drawableHeight);
        super.setCompoundDrawables(left, top, right, bottom);
    }
}
