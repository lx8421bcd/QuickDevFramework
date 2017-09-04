package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.linxiao.framework.R;


/**
 * 小红点控件
 * <p>在没有字符时,默认为使用系统红色,大小为8dp * 8dp的小红点,
 * 通过badge_color属性可以设置红点颜色,badge_default_size属性可设置红点默认大小.
 * 在向其设置字符时,如果字符长度>5之后的内容将会被省略,如果设置数字,当数字大于99时,默认将显示99+,
 * 可以通过setTargetView()方法,在java代码中绑定至指定View</p>
 *
 * Create on 2015-11-03
 * @author linxiao
 * @version 1.0
 */
public class BadgeView extends android.support.v7.widget.AppCompatTextView {
    
    private static final String TAG = BadgeView.class.getSimpleName();
    
    private int minPaddingHorizontal = dip2Px(8);
    private int minPaddingVertical = dip2Px(1) / 2;
    
    private int badgeColor = Color.RED;
    private float radius;
    
    private int defaultSize = dip2Px(8);
    private boolean hideOnZero = false;
    
    //省略标识
    private String ellipsis = "99+";
    private int ellipsisDigit = 2;
    
    public BadgeView(Context context) {
        super(context);
        init(context, null);
    }
    
    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
            badgeColor = typedArray.getColor(R.styleable.BadgeView_badge_color, Color.RED);
            defaultSize = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badge_defaultSize, dip2Px(8));
            hideOnZero = typedArray.getBoolean(R.styleable.BadgeView_badge_hideOnZero, false);
            ellipsis = typedArray.getString(R.styleable.BadgeView_badge_numberEllipsis);
            ellipsisDigit = typedArray.getInt(R.styleable.BadgeView_badge_ellipsisDigit, 2);
            typedArray.recycle();
        }
        // 没有自定义省略符号，使用默认的数字省略符号
        if (TextUtils.isEmpty(ellipsis)) {
            countEllipsisString();
        }
        //在自定义属性初始化后重新setText
        if (!TextUtils.isEmpty(getText())) {
            setText(getText());
        }
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
    }
    
    private void countEllipsisString() {
        ellipsis = "";
        for (int i = 0; i < ellipsisDigit; i++) {
            ellipsis += "9";
        }
        ellipsis += "+";
    }
    
    /**
     * 设置在显示数字为0的时候隐藏小红点
     *
     * @param hideOnZero 是否隐藏
     * */
    public void setHideOnZero(boolean hideOnZero) {
        this.hideOnZero = hideOnZero;
        setText(getText());
    }
    
    /**
     * 设置消息, 数字大于99时显示"99+",字符串长度大于5的部分省略
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (ellipsisDigit == 0) {
            // 此时为TextView基类调用setText，子类属性还未初始化，
            // 不作任何判断直接执行基类操作
            super.setText(text, type);
            return;
        }
        if (text == null) {
            return;
        }
        if (text.toString().matches("^\\d+$")) {
            int number = Integer.parseInt(text.toString());
            if (number == 0 && hideOnZero) {
                hide();
            }
            else {
                show();
            }
            if (text.length() > ellipsisDigit) {
                text = ellipsis;
            }
        } else if (text.length() > 5) {
            text = text.subSequence(0, 4) + "...";
        }
        
        super.setText(text, type);
        requestLayout();
    }
    
    /**
     * 设置红点背景,在字符数为1时显示原型,在字符数超过1时显示圆角矩形
     * */
    private void setBadgeBackground() {
        float[] radiusArray = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRect = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable defaultBgDrawable = new ShapeDrawable(roundRect);
        defaultBgDrawable.getPaint().setColor(badgeColor);
        super.setBackgroundDrawable(defaultBgDrawable);
    }
    
    public void show() {
        this.setVisibility(View.VISIBLE);
    }
    
    public void hide() {
        this.setVisibility(View.INVISIBLE);
    }
    
    /**
     * 将红点绑定到某个现有控件上
     * @param target 目标控件
     * @param badgeGravity 红点相对目标控件位置
     * @param marginLeft 左外边距
     * @param marginTop 上外边距
     * @param marginRight 右外边距
     * @param marginBottom 下外边距
     * */
    public void setTargetView(View target, int badgeGravity, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (target == null) {
            return;
        }
        if (target.getParent() instanceof FrameLayout) {
            ((FrameLayout) target.getParent()).addView(this);
            
        } else if (target.getParent() instanceof ViewGroup) {
            // use a new FrameLayout container for adding badge
            ViewGroup parentContainer = (ViewGroup) target.getParent();
            int groupIndex = parentContainer.indexOfChild(target);
            parentContainer.removeView(target);
            
            FrameLayout badgeContainer = new FrameLayout(getContext());
            ViewGroup.LayoutParams parentLayoutParams = target.getLayoutParams();
            
            badgeContainer.setLayoutParams(parentLayoutParams);
            target.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            
            parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams);
            badgeContainer.addView(target);
            
            badgeContainer.addView(this);
            FrameLayout.LayoutParams badgeLayoutParam = (FrameLayout.LayoutParams) this.getLayoutParams();
            badgeLayoutParam.gravity = badgeGravity;
            badgeLayoutParam.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        } else if (target.getParent() == null) {
            Log.e(getClass().getSimpleName(), "ParentView is needed");
        }
    }
    /**
     * 将红点绑定到某个控件上，默认为右上方，各方向margin为 0
     *
     * @param target 目标控件
     * */
    public void setTargetView(View target) {
        setTargetView(target, Gravity.END, 0, 0, 0, 0);
    }
    
    /**
     * 将红点绑定到某个控件上，默认各方向margin为 0
     *
     * @param target 目标控件
     * @param badgeGravity 相对位置
     * */
    public void setTargetView(View target, int badgeGravity) {
        setTargetView(target, badgeGravity, 0, 0, 0, 0);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLength = this.getText().length();
        if (textLength <= 0) {
            setMeasuredDimension(defaultSize, defaultSize);
            return;
        }
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        radius = bottom - top;
        setBadgeBackground();
    }
    
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        int textLength = this.getText().length();
        if (textLength <= 0) {
            super.setPadding(0, 0, 0, 0);
            return;
        }
        int padding = Math.max(Math.max(left, right), Math.max(top, bottom));
        int textWidth = (int) (getPaint().measureText(getText().toString()));
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.top) + 2);
        Log.d(TAG, "TextLength = " + textLength + ", TextWidth = " + textWidth + ", TextHeight = " + textHeight + ", padding = " + padding);
        
        if (textLength == 1) {
            // 在为单个字符时, 根据文字宽高计算出的水平/垂直方向补充padding, 使得控件为正方形
            // 此处根据文字宽高计算，至少有一个补充值为0
            int extraPaddingHorizontal = getExtraPaddingHorizontal();
            int extraPaddingVertical = getExtraPaddingVertical();
            super.setPadding(
                    padding + extraPaddingHorizontal,
                    padding + extraPaddingVertical,
                    padding + extraPaddingHorizontal,
                    padding + extraPaddingVertical
            );
            return;
        }
        super.setPadding(
                minPaddingHorizontal + padding,
                minPaddingVertical + padding,
                minPaddingHorizontal + padding,
                minPaddingVertical +padding
        );
    }
    
    @Override
    public int getPaddingLeft() {
        if (super.getPaddingLeft() == 0) {
            return 0;
        }
        int textLength = this.getText().length();
        if (textLength == 1) {
            return super.getPaddingLeft() - getExtraPaddingHorizontal();
        }
        if (textLength > 1) {
            return super.getPaddingLeft() - minPaddingHorizontal;
        }
        return super.getPaddingLeft();
    }
    
    @Override
    public int getPaddingRight() {
        if (super.getPaddingRight() == 0) {
            return 0;
        }
        int textLength = this.getText().length();
        if (textLength == 1) {
            return super.getPaddingRight() - getExtraPaddingHorizontal();
        }
        if (textLength > 1) {
            return super.getPaddingRight() - minPaddingHorizontal;
        }
        return super.getPaddingRight();
    }
    
    @Override
    public int getPaddingTop() {
        if (super.getPaddingTop() == 0) {
            return 0;
        }
        int textLength = this.getText().length();
        if (textLength == 1) {
            return super.getPaddingTop() - getExtraPaddingVertical();
        }
        if (textLength > 1) {
            return super.getPaddingTop() - minPaddingVertical;
        }
        return super.getPaddingTop();
    }
    
    @Override
    public int getPaddingBottom() {
        if (super.getPaddingBottom() == 0) {
            return 0;
        }
        int textLength = this.getText().length();
        if (textLength == 1) {
            return super.getPaddingBottom() - getExtraPaddingVertical();
        }
        if (textLength > 1) {
            return super.getPaddingBottom() - minPaddingVertical;
        }
        return super.getPaddingBottom();
    }
    
    
    private int getExtraPaddingHorizontal() {
        if (this.getText().length() != 1) {
            return 0 ;
        }
        int textWidth = (int) (getPaint().measureText(getText().toString()));
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.top) + 2);
        if (textHeight <= textWidth) {
            return 0;
        }
        return (textHeight + getCompoundPaddingBottom() + getCompoundPaddingTop() - textWidth) / 2;
    }
    
    private int getExtraPaddingVertical() {
        if (this.getText().length() != 1) {
            return 0 ;
        }
        int textWidth = (int) (getPaint().measureText(getText().toString()));
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.top) + 2);
        if (textWidth <= textHeight) {
            return 0;
        }
        return (textWidth + getCompoundPaddingLeft() + getCompoundPaddingRight() - textHeight) / 2;
    }
    
    @Override
    public void setBackground(Drawable background) {
        setBadgeBackground();
    }
    
    @Override
    public void setBackgroundColor(int color) {
        badgeColor = color;
        setBadgeBackground();
    }
    
    @Override
    public void setBackgroundDrawable(Drawable background) {
        setBadgeBackground();
    }
    
    @Override
    public void setBackgroundResource(int resid) {
        setBadgeBackground();
    }
    
    private int dip2Px(float dip) {
        return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
    }
}
