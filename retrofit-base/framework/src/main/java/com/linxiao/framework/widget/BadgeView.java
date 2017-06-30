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
 * 在向其设置字符时,如果字符长度>5之后的内容将会被省略,如果设置数字,当数字大于99时,将显示99+,
 * 可以通过setTargetView()方法,在java代码中绑定至指定View</p>
 * TODO:让小红点Padding可设置, 使用自定义属性
 * @author linxiao
 * @version 1.0
 */
public class BadgeView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = BadgeView.class.getSimpleName();

    private int badgeColor = Color.RED;

    private int defaultSize = dip2Px(8);
    private float radius;
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
     * 设置消息, 数字大于99时显示"99+",字符串长度大于5的部分省略
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null) {
            return;
        }
        if (ellipsisDigit == 0) { //此时为TextView基类调用setText，不作任何判断直接执行基类操作
            super.setText(text, type);
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
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(badgeColor);
        super.setBackgroundDrawable(bgDrawable);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
    }

    /**
     * Attach the BadgeView to the target view
     *
     * @param target the view to attach the BadgeView
     */
    public void setTargetView(View target) {
        setTargetView(target, Gravity.END, 0, 0, 0, 0);
    }

    public void setTargetView(View target, int badgeGravity) {
        setTargetView(target, badgeGravity, 0, 0, 0, 0);
    }

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLength = this.getText().length();
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (textLength == 0) {
            //在无字符时设置控件为8 * 8 dp的小红点
            setPadding(0, 0, 0, 0);
            setMeasuredDimension(defaultSize, defaultSize);
        } else if (textLength == 1) {
            //在为单个字符时,计算根据上面计算的字体宽高,设置合适的内边距使得控件为正方形
            int width = (int) (getPaint().measureText(getText().toString()));
            Paint.FontMetrics fm = getPaint().getFontMetrics();
            int height = (int) (Math.ceil(fm.descent - fm.top) + 2);
            height += getPaddingBottom() + getPaddingTop() + getCompoundPaddingBottom() + getCompoundPaddingTop();
            int padding = (height - width) / 2;
            setPadding(padding, 0, padding, 0);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            //在多个字符时设置合适的外边距
            setPadding((int) getTextSize() / 2, dip2Px(1) / 2, (int) getTextSize() / 2, dip2Px(1) / 2);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        radius = bottom - top;
        setBadgeBackground();
    }

    @Override
    public void setBackground(Drawable background) {
        setBadgeBackground();
    }

    @Override
    public void setBackgroundColor(int color) {
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

    public void setHideOnZero(boolean hideOnZero) {
        this.hideOnZero = hideOnZero;
    }

    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }


}
