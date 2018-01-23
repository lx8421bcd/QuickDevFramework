package com.linxiao.framework.widget.pullrefresh;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * 自定义下拉刷新布局
 * <p>
 * 下拉刷新头部使用{@link RefreshView} 铺成 MATCH_PARENT 的布局，
 * 各种下拉刷新效果直接继承 RefreshView 并在其中实现即可
 * </p>
 * Created by linxiao on 2017/6/21.
 */
public class PullToRefreshLayout extends ViewGroup {
    private static final String TAG = PullToRefreshLayout.class.getSimpleName();
    
    public interface OnRefreshListener {
        void onRefresh();
    }
    
    public interface OnInterceptTouchEventListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }
    
    private static final int INVALID_POINTER = -1;
    // 最大下拉距离 单位dp
    private static final int DEFAULT_MAX_DRAG_DISTANCE = 80;
    // 滑动阻尼
    private static final float DRAG_RATE = .5f;
    
    // 下拉刷新动画View
    private RefreshView mRefreshView;
    // 下拉刷新子容器
    private View mTargetView;
    // 插值器，用于回弹动画
    private Interpolator mDecelerateInterpolator;
    // 滑动判断距离
    private int mTouchSlop;
    private int mSpinnerFinalOffset;
    // 滑动总距离
    private int mTotalDragDistance;
    // 初始Y轴坐标
    private float mInitialMotionY;
    // 滑动距离百分比
    private float mDragPercent;
    // 回弹时间
    public int mDurationToStartPosition;
    public int mDurationToCorrectPosition;
    // 初始偏移量
    private int mInitialOffsetTop;
    // 当前距离顶部偏移量
    private int mCurrentOffsetTop;
    // 动画缓存值
    private int mFrom;
    // 触发下拉的触摸点Id
    private int mActivePointerId;
    // 是否正在刷新
    private boolean refreshing = false;
    // 是否正在拉动
    private boolean isDragging;
    // 是否向下层容器传递触控事件
    private boolean dispatchTouchDown;
    // 默认下拉刷新监听器
    private OnRefreshListener mOnRefreshListener;
    // 事件拦截监听器
    private OnInterceptTouchEventListener mInterceptListener;
    
    
    private Animation moveToStartAnimation = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = mFrom - (int) (mFrom * interpolatedTime);
            int offset = targetTop - mTargetView.getTop();
            setOffsetTop(offset, false);
        }
    };
    
    private Animation moveToRefreshAnimation = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mSpinnerFinalOffset;
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTargetView.getTop();
            setOffsetTop(offset, false);
        }
    };
    
    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mRefreshView.setVisibility(View.VISIBLE);
            if (refreshing) {
                mRefreshView.startRefreshAnim();
            }
        }
        
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        
        @Override
        public void onAnimationEnd(Animation animation) {
            if (!refreshing) {
                mRefreshView.stopRefreshAnim();
                moveToStartPosition();
            }
            mCurrentOffsetTop = mTargetView.getTop();
        }
    };
    
    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mRefreshView.stopRefreshAnim();
        }
        
        @Override
        public void onAnimationRepeat(Animation animation) {}
        
        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrentOffsetTop = mTargetView.getTop();
        }
    };
    
    
    public PullToRefreshLayout(Context context) {
        super(context);
        init(context, null);
    }
    
    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDurationToStartPosition = getResources().getInteger(android.R.integer.config_longAnimTime);
        mDurationToCorrectPosition = getResources().getInteger(android.R.integer.config_longAnimTime);
        mSpinnerFinalOffset = mTotalDragDistance = dp2px(DEFAULT_MAX_DRAG_DISTANCE);
        
        mDecelerateInterpolator = new DecelerateInterpolator(2);
        
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

//        setRefreshView(new QiNiangRefreshView(context));
        setRefreshView(new DefaultRefreshView(context));
    }
    
    /**
     * 设置下拉刷新显示头布局
     * */
    public void setRefreshView(RefreshView refreshView) {
        if (refreshView == null) {
            return;
        }
        removeView(mRefreshView);
        mRefreshView = refreshView;
        mRefreshView.setVisibility(View.GONE);
        addView(mRefreshView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSpinnerFinalOffset = mTotalDragDistance = mRefreshView.getMaxDragDistance();
    }
    
    private void captureTargetView() {
        if (mTargetView != null || getChildCount() <= 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!child.equals(mRefreshView)) {
                mTargetView = child;
            }
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        captureTargetView();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingRight() - getPaddingLeft(),
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                MeasureSpec.EXACTLY);
        if (mRefreshView != null) {
            mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mTargetView != null) {
            mTargetView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        captureTargetView();
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        try {
            if (mRefreshView != null) {
                mRefreshView.layout(
                        left,
                        top,
                        left + width - right,
                        top + height - bottom
                );
            }
            if (mTargetView != null) {
                mTargetView.layout(
                        left,
                        top + mTargetView.getTop(),
                        left + width - right,
                        top + height - bottom + mTargetView.getTop()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setRefreshing(false);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || (canChildScrollUp() && !refreshing)) {
            return false;
        }
        if (mInterceptListener != null && !mInterceptListener.onInterceptTouchEvent(ev)) {
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (!refreshing) {
                setOffsetTop(0, true);
            }
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            isDragging = false;
            final float initialMotionY = getMotionY(ev, mActivePointerId);
            if (initialMotionY == -1) {
                return false;
            }
            mInitialMotionY = initialMotionY;
            mInitialOffsetTop = mCurrentOffsetTop;
            dispatchTouchDown = false;
            mDragPercent = 0;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mActivePointerId == INVALID_POINTER) {
                return false;
            }
            final float y = getMotionY(ev, mActivePointerId);
            if (y == -1) {
                return false;
            }
            final float yDiff = y - mInitialMotionY;
            if (refreshing) {
                isDragging = !(yDiff < 0 && mCurrentOffsetTop <= 0);
            } else if (yDiff > mTouchSlop && !isDragging) {
                isDragging = true;
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            isDragging = false;
            mActivePointerId = INVALID_POINTER;
            break;
        case MotionEventCompat.ACTION_POINTER_UP:
            onSecondaryPointerUp(ev);
            break;
        }
        
        return isDragging;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDragging) {
            return super.onTouchEvent(event);
        }
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
        case MotionEvent.ACTION_MOVE:
            final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
            if (pointerIndex < 0) {
                return false;
            }
            final float y = MotionEventCompat.getY(event, pointerIndex);
            final float yDiff = y - mInitialMotionY;
            int targetY;
            if (refreshing) {
                targetY = (int) (mInitialOffsetTop + yDiff);
                if (canChildScrollUp()) {
                    targetY = -1;
                    mInitialMotionY = y;
                    mInitialOffsetTop = 0;
                }
                if (targetY < 0) {
                    MotionEvent obtain = event;
                    if (!dispatchTouchDown) {
                        obtain = MotionEvent.obtain(event);
                        obtain.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchDown = true;
                    }
                    mTargetView.dispatchTouchEvent(obtain);
                }
                else if (targetY > mTotalDragDistance) {
                    targetY = mTotalDragDistance;
                }
                else {
                    if (dispatchTouchDown) {
                        MotionEvent obtain = MotionEvent.obtain(event);
                        obtain.setAction(MotionEvent.ACTION_CANCEL);
                        dispatchTouchDown = false;
                        mTargetView.dispatchTouchEvent(obtain);
                    }
                }
            }
            else {
                float scrollTop = yDiff * DRAG_RATE; //* DRAG_RATE
                float originalDragPercent = scrollTop / mTotalDragDistance;
                if (originalDragPercent < 0) {
                    return false;
                }
                mDragPercent = Math.min(1f, Math.abs(originalDragPercent));
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mSpinnerFinalOffset;
                float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent;
                targetY = (int) ((slingshotDist * mDragPercent) + extraMove);
                if (mRefreshView.getVisibility() != View.VISIBLE) {
                    mRefreshView.setVisibility(View.VISIBLE);
                }
            }
            setOffsetTop(targetY - mCurrentOffsetTop, true);
            break;
        case MotionEventCompat.ACTION_POINTER_DOWN:
            final int index = MotionEventCompat.getActionIndex(event);
            mActivePointerId = MotionEventCompat.getPointerId(event, index);
            break;
        case MotionEventCompat.ACTION_POINTER_UP:
            onSecondaryPointerUp(event);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mActivePointerId == INVALID_POINTER) {
                return false;
            }
            if (refreshing) {
                if (dispatchTouchDown) {
                    mTargetView.dispatchTouchEvent(event);
                    dispatchTouchDown = false;
                }
                return false;
            }
            final int pointerIdx = MotionEventCompat.findPointerIndex(event, mActivePointerId);
            final float yPos = MotionEventCompat.getY(event, pointerIdx);
            final float overScrollTop = (yPos - mInitialMotionY) * DRAG_RATE;
            isDragging = false;
            if (overScrollTop > mTotalDragDistance) {
                setRefreshing(true);
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            } else {
                refreshing = false;
                moveToStartPosition();
            }
            mActivePointerId = INVALID_POINTER;
            return false;
        }
        
        return true;
    }
    
    public void setRefreshing(boolean refreshing) {
        if (this.refreshing == refreshing) {
            return;
        }
        this.refreshing = refreshing;
        captureTargetView();
        if (refreshing) {
            moveToRefreshPosition();
        } else {
            moveToStartPosition();
        }
    }
    
    public boolean isRefreshing() {
        return refreshing;
    }
    
    private void moveToStartPosition() {
        mFrom = mCurrentOffsetTop;
        moveToStartAnimation.reset();
        moveToStartAnimation.setDuration(mDurationToStartPosition);
        moveToStartAnimation.setInterpolator(mDecelerateInterpolator);
        moveToStartAnimation.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(moveToStartAnimation);
    }
    
    private void moveToRefreshPosition() {
        mFrom = mCurrentOffsetTop;
        moveToRefreshAnimation.reset();
        moveToRefreshAnimation.setDuration(mDurationToCorrectPosition);
        moveToRefreshAnimation.setInterpolator(mDecelerateInterpolator);
        moveToRefreshAnimation.setAnimationListener(mRefreshListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(moveToRefreshAnimation);
    }
    
    private boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mTargetView, -1);
    }
    
    private float getMotionY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }
    
    private void setOffsetTop(int offset, boolean requiresUpdate) {
        mTargetView.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTargetView.getTop();
        mRefreshView.setDragOffset(offset);
        if (requiresUpdate) {
            invalidate();
        }
    }
    
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }
    
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }
    
    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener listener) {
        mInterceptListener = listener;
    }
    
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

}
