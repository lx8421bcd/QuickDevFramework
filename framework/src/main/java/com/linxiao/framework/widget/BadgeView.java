package com.linxiao.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linxiao.framework.R;


/**
 * 小红点控件
 * <p>在没有字符时,默认为使用系统红色,大小为8dp * 8dp的小红点,
 * 在向其设置字符时,如果字符长度>5之后的内容将会被省略,如果设置数字,当数字大于99时,默认将显示99+,
 * 可以通过setTargetView()方法,在java代码中绑定至指定View</p>
 * <p>
 * 自定义属性：<br>
 * badge_color: 设置红点颜色 <br>
 * badge_default_size: 红点在无文字显示时的默认大小 <br>
 * badge_hideOnZero: 在显示数字为0时是否隐藏 <br>
 * badge_ellipsisDigit: 设置超限位数，超过位数后将显示超限省略符号，默认两位 <br>
 * badge_numberEllipsis: 在数字超限时的省略符号，默认显示99+ <br>
 * badge_strokeWidth: 小圆点边框宽度
 * badge_strokeColor: 小圆点边框颜色
 * </p>
 *
 * Create on 2015-11-03
 * @author linxiao
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
public class BadgeView extends TextView {

    private static final String TAG = BadgeView.class.getSimpleName();

    private int minPaddingHorizontal = dip2Px(4);
    private int minPaddingVertical = dip2Px(0.5f);

    private int badgeColor = Color.RED;
    private float radius;

    private int defaultSize = dip2Px(8);
    private boolean hideOnZero = false;

    //省略标识
    private String ellipsis = "99+";
    private int ellipsisDigit = 2;
    //补充内边距值，内容为1字符时的内边距值，
    private int extraPaddingHorizontal;
    private int extraPaddingVertical;
    //是否绑定到目标，防止重复添加
    private FrameLayout badgeContainer;
    //缓存padding
    private int cachePaddingLeft;
    private int cachePaddingTop;
    private int cachePaddingRight;
    private int cachePaddingBottom;

    // 边框参数
    private int strokeWidth = 0;
    private int strokeColor = 0;

    public BadgeView(Context context) {
        super(context);
        init(context, null);
        setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        setTextSize(12);
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
            badgeColor = typedArray.getColor(R.styleable.BadgeView_badge_color, badgeColor);
            defaultSize = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badge_defaultSize, dip2Px(8));
            hideOnZero = typedArray.getBoolean(R.styleable.BadgeView_badge_hideOnZero, false);
            ellipsis = typedArray.getString(R.styleable.BadgeView_badge_numberEllipsis);
            ellipsisDigit = typedArray.getInt(R.styleable.BadgeView_badge_ellipsisDigit, 2);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badge_strokeWidth, 0);
            strokeColor= typedArray.getColor(R.styleable.BadgeView_badge_strokeColor, 0);
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
        execSetPadding();
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
     */
    public void setHideOnZero(boolean hideOnZero) {
        this.hideOnZero = hideOnZero;
        setText(getText());
    }

    /**
     * 设置小红点默认大小
     */
    public void setDefaultBadgeSize(int defaultSize) {
        this.defaultSize = defaultSize;
        requestLayout();
    }

    /**
     * 设置两位数最小内边距
     */
    public void setMinPaddingOverOneDigit(int horizontal, int vertical) {
        minPaddingHorizontal = horizontal;
        minPaddingVertical = vertical;
        requestLayout();
    }

    /**
     * 设置数字
     * <p>仅为代理setText方法将数字toString，防止使用setText设置数字时误被当做资源ID引起崩溃</p>
     */
    public void setNumber(int i) {
        setText(String.valueOf(i));
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
            } else {
                show();
            }
            if (text.length() > ellipsisDigit) {
                text = ellipsis;
            }
        } else if (text.length() > 5) {
            text = text.subSequence(0, 4) + "...";
        }
        super.setText(text, type);
        execSetPadding();
    }

    public void setBadgeStroke(int width, int color) {
        strokeWidth = width;
        strokeColor = color;
        setBadgeBackground();
    }

    /**
     * 设置红点背景,在字符数为1时显示原型,在字符数超过1时显示圆角矩形
     */
    private void setBadgeBackground() {
        GradientDrawable defaultBgDrawable = new GradientDrawable();
        defaultBgDrawable.setCornerRadius(radius);
        defaultBgDrawable.setColor(badgeColor);
        if (strokeWidth != 0 && strokeColor != 0) {
            defaultBgDrawable.setStroke(strokeWidth, strokeColor);
        }
        super.setBackgroundDrawable(defaultBgDrawable);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    /**
     * 解除小红点对某一View的绑定
     * <p>此操作将会清除{@link #setTargetView(View)}方法在目标View外套的FrameLayout，
     * 还原目标View原本的状态</p>
     */
    public void unbindTargetView() {
        if (badgeContainer == null || badgeContainer.getChildCount() <= 0) {
            return;
        }
        View lastTarget = badgeContainer.getChildAt(0);
        if (lastTarget != null) {
            ViewGroup lastParent = (ViewGroup) badgeContainer.getParent();
            ViewGroup.LayoutParams lastLayoutParams = badgeContainer.getLayoutParams();

            badgeContainer.removeView(lastTarget);
            lastParent.removeView(badgeContainer);

            lastTarget.setLayoutParams(lastLayoutParams);
            lastParent.addView(lastTarget);
        }
        badgeContainer.removeAllViews();
        badgeContainer = null;
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
    public void setTargetView(View target, int badgeGravity, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (target == null) {
            return;
        }
        if (target.getParent() instanceof ViewGroup) {
            ViewGroup parentContainer = (ViewGroup) target.getParent();
            if (parentContainer.equals(badgeContainer)) {
                //对同一个目标执行setTargetView;
                FrameLayout.LayoutParams badgeLayoutParam = (FrameLayout.LayoutParams) this.getLayoutParams();
                badgeLayoutParam.gravity = badgeGravity;
                badgeLayoutParam.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                return;
            }
            unbindTargetView();
            int groupIndex = parentContainer.indexOfChild(target);
            parentContainer.removeView(target);
            badgeContainer = new FrameLayout(getContext());
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
     * 将红点绑定到某个控件上，默认各方向margin为 0
     *
     * @param target       目标控件
     * @param badgeGravity 相对位置
     */
    public void setTargetView(View target, int badgeGravity) {
        setTargetView(target, badgeGravity, 0, 0, 0, 0);
    }

    /**
     * 将红点绑定到某个控件上，默认为右上方，各方向margin为 0
     *
     * @param target 目标控件
     */
    public void setTargetView(View target) {
        setTargetView(target, Gravity.END, 0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLength = this.getText().length();
        if (textLength <= 0) {
            setMeasuredDimension(defaultSize, defaultSize);
            return;
        }
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
//        if (mode != MeasureSpec.EXACTLY) {
//            execSetPadding();
//        }
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
        cachePaddingLeft = left;
        cachePaddingTop = top;
        cachePaddingRight = right;
        cachePaddingBottom = bottom;
        execSetPadding();

    }

    private void execSetPadding() {
        if (ellipsisDigit == 0) {
            // 此时为TextView基类调用setText，子类属性还未初始化，
            // 不作任何判断直接执行基类操作
            super.setPadding(cachePaddingLeft, cachePaddingTop, cachePaddingRight, cachePaddingBottom);
            return;
        }
        int textLength = 0;
        if (this.getText() != null) {
            textLength = this.getText().length();
        }
        if (textLength == 0) {
            super.setPadding(cachePaddingLeft, cachePaddingTop, cachePaddingRight, cachePaddingBottom);
            return;
        }
        if (textLength == 1) {
            int padding = Math.max(
                    Math.max(cachePaddingLeft, cachePaddingRight),
                    Math.max(cachePaddingTop, cachePaddingBottom));
            padding += minPaddingVertical;
            // 在为单个字符时, 根据文字宽高计算出的水平/垂直方向补充padding, 使得控件为正方形
            calculateExtraPadding();
            super.setPadding(
                    padding + extraPaddingHorizontal,
                    padding + extraPaddingVertical,
                    padding + extraPaddingHorizontal,
                    padding + extraPaddingVertical
            );
            return;
        }
        int paddingHorizontal = Math.max(cachePaddingLeft, cachePaddingRight);
        int paddingVertical = Math.max(cachePaddingTop, cachePaddingBottom);
        super.setPadding(
                minPaddingHorizontal + paddingHorizontal,
                minPaddingVertical + paddingVertical,
                minPaddingHorizontal + paddingHorizontal,
                minPaddingVertical + paddingVertical
        );
    }

    @Override
    public int getPaddingLeft() {
        if (super.getPaddingLeft() == 0) {
            return 0;
        }
        int textLength = this.getText().length();
        if (textLength == 0) {
            return 0;
        }
        if (textLength == 1) {
            return super.getPaddingLeft() - extraPaddingHorizontal;
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
        if (textLength == 0) {
            return 0;
        }
        if (textLength == 1) {
            return super.getPaddingRight() - extraPaddingHorizontal;
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
        if (textLength == 0) {
            return 0;
        }
        if (textLength == 1) {
            return super.getPaddingTop() - extraPaddingVertical;
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
        if (textLength == 0) {
            return 0;
        }
        if (textLength == 1) {
            return super.getPaddingBottom() - extraPaddingVertical;
        }
        if (textLength > 1) {
            return super.getPaddingBottom() - minPaddingVertical;
        }
        return super.getPaddingBottom();
    }


    private void calculateExtraPadding() {
        if (this.getText().length() != 1) {
            extraPaddingHorizontal = 0;
            extraPaddingVertical = 0;
            return;
        }
        // 此处根据文字宽高计算，至少有一个补充值为0
        int textWidth = (int) (getPaint().measureText(getText().toString()));
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.top) + 2);
        if (textWidth > textHeight) {
            extraPaddingHorizontal = 0;
            extraPaddingVertical = (textWidth - textHeight) / 2;
        } else if (textHeight > textWidth) {
            extraPaddingHorizontal = (textHeight - textWidth) / 2;
            extraPaddingVertical = 0;
        } else {
            extraPaddingHorizontal = 0;
            extraPaddingVertical = 0;
        }
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
    public void setBackgroundResource(int resId) {
        setBadgeBackground();
    }

    private int dip2Px(float dip) {
        return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
    }
}