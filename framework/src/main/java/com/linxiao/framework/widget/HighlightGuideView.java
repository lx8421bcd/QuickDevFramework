package com.linxiao.framework.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 高亮引导控件
 * <p> 对于单个引导页创建，请使用 {@link #newInstance(Activity)} 创建引导页实例，
 * 如果有依次弹出若干引导页的需求，可以使用{@link #newGuideQueue()}创建引导页队列，
 * 队列会自动管理引导页销毁逻辑
 * </p>
 *
 * Created by linxiao on 2017/8/3.
 */
public class HighlightGuideView extends FrameLayout {
    /**
     * 方形高亮
     * */
    public static final int STYLE_RECT = 0;
    /**
     * 原型高亮
     * */
    public static final int STYLE_CIRCLE = 1;
    /**
     * 椭圆形高亮
     * */
    public static final int STYLE_OVAL = 2;

    /**
     * 没有高亮目标的引导控件容器ID
     * */
    private static final int NO_TARGET_GUIDE_ID = 10152;

    /**
     * 引导页销毁监听
     * */
    public interface OnDismissListener {
        void onDismiss();
    }
    
    // 需要高亮页面的根布局
    private ViewGroup mRootView;

    private Map<Integer, View> mTargetViewMap = new ArrayMap<>();
    private Map<Integer, List<View>> mGuideViewsMap = new ArrayMap<>();
    private Map<Integer, Integer> mHighlightPaddingMap = new ArrayMap<>();
    private Map<Integer, Map<String, Integer>> mGuideRelativePosMap = new ArrayMap<>();

    private int mHighlightStyle = STYLE_CIRCLE; // 默认为圆形高亮
    // 绘制参数
    private Bitmap backgroundBitmap; //背景
    private Paint mHighlightPaint;
    private Canvas mCanvas;
    private int screenWidth;
    private int screenHeight;
    private int backgroundColor = 0xCC000000; //背景默认颜色
    
    private boolean touchOutsideCancelable = true;
    
    private List<OnDismissListener> dismissListeners = new ArrayList<>();
    
    /**
     * 新建引导页实例
     * <p>必须使用Activity</p>
     * */
    public static HighlightGuideView newInstance(Activity activity) {
        return new HighlightGuideView(activity);
    }

    /**
     * 新建引导页队列
     * */
    public static GuideQueue newGuideQueue() {
        return new GuideQueue();
    }

    private HighlightGuideView(@NonNull Context context) {
        super(context);
        if (context instanceof Activity) {
            mRootView = (ViewGroup) ((Activity)context).findViewById(Window.ID_ANDROID_CONTENT);
        }
        setWillNotDraw(false);
        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG); // 开启抗锯齿和抗抖动
        mHighlightPaint.setARGB(0, 255, 0, 0);
        mHighlightPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        
        backgroundBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(backgroundBitmap);
        mCanvas.drawColor(backgroundColor);
    }

    private HighlightGuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context);
    }

    private HighlightGuideView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context);
    }

    /**
     * 设置高亮导航目标
     *
     * @param view 目标控件
     * */
    public HighlightGuideView addTargetView(View view) {
        mTargetViewMap.put(view.hashCode(), view);
        if (!mGuideViewsMap.containsKey(view.hashCode())) {
            mGuideViewsMap.put(view.hashCode(), new ArrayList<View>());
        }
        return this;
    }

    /**
     * 设置目标View高亮区域内边距
     * @param view 目标View
     * @param padding 内边距
     * */
    public HighlightGuideView setHighlightPadding(View view, int padding) {
        if (view == null) {
            return this;
        }
        mHighlightPaddingMap.put(view.hashCode(), padding);
        return this;
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
     * */
    public HighlightGuideView addGuideView(View targetView, View guideView, int width, int height, int relativeX, int relativeY) {
        if (targetView != null && !mGuideViewsMap.containsKey(targetView.hashCode())) {
            return this;
        }
        LayoutParams lp = new LayoutParams(width, height);
        guideView.setLayoutParams(lp);
        
        Map<String, Integer> paramsMap = new ArrayMap<>();
        paramsMap.put("x", relativeX);
        paramsMap.put("y", relativeY);
        mGuideRelativePosMap.put(guideView.hashCode(), paramsMap);

        if (targetView != null) {
            mGuideViewsMap.get(targetView.hashCode()).add(guideView);
        }
        else {
            if (!mGuideViewsMap.containsKey(NO_TARGET_GUIDE_ID)) {
                mGuideViewsMap.put(NO_TARGET_GUIDE_ID, new ArrayList<View>());
            }
            mGuideViewsMap.get(NO_TARGET_GUIDE_ID).add(guideView);
        }
        this.addView(guideView);
        return this;
    }
    
    /**
     * 添加目标引导控件
     * <p>不传入宽高默认为wrap_content</p>
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param guideView 图片资源Drawable
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     * */
    public HighlightGuideView addGuideView(View targetView, View guideView, int relativeX, int relativeY) {
        addGuideView(targetView, guideView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                relativeX, relativeY
        );
        return this;
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
     * */
    public HighlightGuideView addGuideImage(View targetView, Drawable guideDrawable, int width, int height, int relativeX, int relativeY) {
        ImageView guideImageView = new ImageView(getContext());
        guideImageView.setImageDrawable(guideDrawable);
        addGuideView(targetView, guideImageView, width, height, relativeX, relativeY);
        return this;
    }
    
    /**
     * 添加引导图片
     * <p>不传入宽高默认为wrap_content</p>
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param guideDrawable 图片资源Drawable
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     * */
    public HighlightGuideView addGuideImage(View targetView, Drawable guideDrawable, int relativeX, int relativeY) {
        addGuideImage(targetView, guideDrawable,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                relativeX, relativeY
        );
        return this;
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
     * */
    public HighlightGuideView addGuideImage(View targetView, @DrawableRes int resId, int width, int height, int relativeX, int relativeY) {
        ImageView guideImageView = new ImageView(getContext());
        guideImageView.setImageResource(resId);
        addGuideView(targetView, guideImageView, width, height, relativeX, relativeY);
        return this;
    }
    
    /**
     * 添加引导图片
     * <p>不传入宽高默认为wrap_content</p>
     *
     * @param targetView 目标View，如果没有目标则传入null
     * @param resId 图片资源ID
     * @param relativeX 引导图片相对目标View左上角横向距离
     * @param relativeY 引导图片相对目标View左上角纵向距离
     * */
    public HighlightGuideView addGuideImage(View targetView, @DrawableRes int resId, int relativeX, int relativeY) {
        addGuideImage(targetView, resId,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                relativeX, relativeY
        );
        return this;
    }

    /**
     * 是否允许点击空白区域隐藏HighlightGuideView
     * <p>默认为true</p>
     *
     * @param cancel 是否允许
     * */
    public HighlightGuideView setCancelOnTouchOutside(boolean cancel) {
        touchOutsideCancelable = cancel;
        return this;
    }

    /**
     * 设置高亮样式
     *
     * @param style 高亮样式
     * */
    public HighlightGuideView setHighlightStyle(int style) {
        if (style > 2 || style < 0) {
            mHighlightStyle = 1;
        }
        mHighlightStyle = style;
        return this;
    }

    /**
     * 设置蒙版背景颜色
     *
     * @param color 颜色色值
     * */
    public HighlightGuideView setMaskBackgroundColor(@ColorInt int color) {
        backgroundColor = color;
        return this;
    }
    
    /**
     * 设置蒙版背景颜色
     *
     * @param resId 颜色资源ID
     * */
    public HighlightGuideView setMaskBackgroundRes(@ColorRes int resId) {
        backgroundColor = ContextCompat.getColor(getContext(), resId);
        return this;
    }
    
    public HighlightGuideView addOnDismissListener(OnDismissListener listener) {
        dismissListeners.add(listener);
        return this;
    }

    /**
     * 显示引导
     * */
    public void show() {
        if (mRootView == null) {
            return;
        }
        for (int i = 0; i < mRootView.getChildCount(); i++) {
            if (mRootView.getChildAt(i).equals(this)) {
                return;
            }
        }
        mRootView.addView(this,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        this.setVisibility(VISIBLE);
    }

    /**
     * 移除引导
     * */
    public void dismiss() {
        if (mRootView == null) {
            return;
        }
        this.setVisibility(GONE);
        mRootView.removeView(this);
        for (OnDismissListener listener : dismissListeners) {
            if (listener != null) {
                listener.onDismiss();
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }
        if (touchOutsideCancelable) {
            dismiss();
        }
        return true;
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int targetId : mGuideViewsMap.keySet()) {
            int targetX = 0, targetY = 0;
            if (targetId != NO_TARGET_GUIDE_ID) {
                View targetView = mTargetViewMap.get(targetId);
                Rect rect = getTargetViewRect(targetView);
                targetX = rect.left;
                targetY = rect.top;
            }
            for (View guideView : mGuideViewsMap.get(targetId)) {
                int relativeX = 0;
                int relativeY = 0;
                if (mGuideRelativePosMap.containsKey(guideView.hashCode())) {
                    relativeX = mGuideRelativePosMap.get(guideView.hashCode()).get("x");
                    relativeY = mGuideRelativePosMap.get(guideView.hashCode()).get("y");
                }

                LayoutParams params = (LayoutParams) guideView.getLayoutParams();
                //尝试兼容View中设定的水平居中或垂直居中属性
                int gravity = params.gravity;
                int absoluteGravity = 0;
                if (Build.VERSION.SDK_INT >= 17) {
                    final int layoutDirection = getLayoutDirection();
                    absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                }
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
                if (absoluteGravity == Gravity.CENTER_HORIZONTAL) {
                    guideView.setX((screenWidth - guideView.getMeasuredWidth()) / 2);
                }
                else {
                    guideView.setX(targetX + params.leftMargin + relativeX);
                }
                if (verticalGravity == Gravity.CENTER_VERTICAL) {
                    guideView.setY((screenHeight - guideView.getMeasuredHeight()) / 2);
                }
                else {
                    guideView.setY(targetY + params.topMargin + relativeY);
                }
            }
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        //绘制高亮区域
        if (mTargetViewMap.size() == 0) {
            return;
        }
        for (int targetId : mGuideViewsMap.keySet()) {
            View targetView = mTargetViewMap.get(targetId);
            int padding = 0;
            if (mHighlightPaddingMap.containsKey(targetId)) {
                padding = mHighlightPaddingMap.get(targetId);
            }
            drawHighlightArea(targetView, padding);
        }
    }
    /**
     * 绘制高亮区域
     * */
    private void drawHighlightArea(View highlightView, int padding) {
        int width = highlightView.getWidth();
        int height = highlightView.getHeight();
        //高亮控件坐标
        Rect targetRect = getTargetViewRect(highlightView);
        int left = targetRect.left;
        int top = targetRect.top;
        int right = targetRect.right;
        int bottom = targetRect.bottom;
    
        RectF highlightRect;
        switch (mHighlightStyle) {
        case STYLE_RECT :
            highlightRect = new RectF(left - padding, top - padding, right + padding, bottom + padding);
            mCanvas.drawRect(highlightRect, mHighlightPaint);
            break;
        case STYLE_OVAL:
            highlightRect = new RectF(left - padding, top - padding, right + padding, bottom + padding);
            mCanvas.drawOval(highlightRect, mHighlightPaint);
            break;
        case STYLE_CIRCLE:
            int radius = ((width > height ? width : height) + padding) / 2;
            mCanvas.drawCircle(left + width / 2, top + height / 2, radius, mHighlightPaint);
            break;
        }
    }
    
    /**
     * 获取目标控件在Activity根布局中的坐标矩阵
     * */
    private Rect getTargetViewRect(View targetView) {
        View parent = mRootView.getChildAt(0);
        View decorView = null;
        Context context = targetView.getContext();
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }
        Rect result = new Rect();
        Rect tmpRect = new Rect();
        
        View tmp = targetView;
        
        if (targetView == parent) {
            targetView.getHitRect(result);
            return result;
        }
        while (tmp != decorView && tmp != parent) {
            tmp.getHitRect(tmpRect);
            if (!tmp.getClass().toString().equals("NoSaveStateFrameLayout")) {
                result.left += tmpRect.left;
                result.top += tmpRect.top;
            }
            tmp = (View) tmp.getParent();
        }
        result.right = result.left + targetView.getMeasuredWidth();
        result.bottom = result.top + targetView.getMeasuredHeight();
        return result;
    }

    /**
     * 引导页队列
     * <p>使用此类构建按添加先后顺序依次显示的引导页队列</p>
     *
     * */
    public static class GuideQueue {

        private List<HighlightGuideView> mGuideViewList = new ArrayList<>();
        private int showCount = 0;

        public void add(HighlightGuideView guideView) {
            if (guideView == null) {
                return;
            }
            guideView.addOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    showNext();
                }
            });
            mGuideViewList.add(guideView);
        }

        /**
         * 移除引导页，显示过后的页面无法移除
         * */
        public void remove(HighlightGuideView guideView) {
            if (mGuideViewList.indexOf(guideView) > showCount) {
                mGuideViewList.remove(guideView);
            }
        }

        public void show() {
            if (mGuideViewList.size() > 0) {
                mGuideViewList.get(0).show();
            }
        }

        /**
         * 取消后续引导显示
         * */
        public void cancelAll() {
            mGuideViewList.get(showCount).dismiss();
            mGuideViewList.clear();
        }

        private void showNext() {
            if (++showCount >= mGuideViewList.size()) {
                mGuideViewList.clear();
                showCount = 0;
                return ;
            }
            mGuideViewList.get(showCount).show();
        }
    }
}
