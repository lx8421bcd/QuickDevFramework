package com.linxiao.framework.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 自定义下拉刷新布局
 * <p>设置了下拉刷新的主题颜色,修复下拉刷新触发与侧滑的冲突</p>
 *
 * @author linxiao
 * @version 1.0
 */
public class CusSwipeRefreshLayout extends SwipeRefreshLayout {

    public interface OnInterceptTouchEventListener {

        /**
         * 拦截触控事件监听,如果此方法返回false则表示放行本次触控事件,
         * 返回true则表示消耗这个触控事件.
         * */
        boolean onInterceptTouchEvent(MotionEvent event);
    }

    private OnInterceptTouchEventListener listener;

    private int mTouchSlop;
    private float mPrevX;

    private boolean isMeasured = false;
    private boolean mPreMeasureRefreshing = false;

    public CusSwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public CusSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //统一设置应用中下拉刷新颜色
//        this.setColorSchemeResources(R.color.red);
    }

    /**
     * 设置在触控事件分发时进行拦截的监听器,用于在有潜在的滑动冲突时判断是否触发下拉刷新
     */
    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //修复在onCreate()时调用setRefreshing不显示加载动画的问题
        if (!isMeasured) {
            isMeasured = true;
            setRefreshing(mPreMeasureRefreshing);
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (isMeasured) {
            super.setRefreshing(refreshing);
        } else {
            mPreMeasureRefreshing = refreshing;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (listener != null && !listener.onInterceptTouchEvent(event)) {
            return false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mPrevX = MotionEvent.obtain(event).getX();
            break;

        case MotionEvent.ACTION_MOVE:
            final float eventX = event.getX();
            float xDiff = Math.abs(eventX - mPrevX);

            if (xDiff > mTouchSlop) {
                return false;
            }
        }

        return super.onInterceptTouchEvent(event);
    }

}
