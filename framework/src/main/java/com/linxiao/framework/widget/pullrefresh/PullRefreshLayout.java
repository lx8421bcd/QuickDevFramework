package com.linxiao.framework.widget.pullrefresh;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
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
import android.widget.FrameLayout;

/**
 * 自定义下拉刷新布局
 * <p>
 * 下拉刷新头部使用{@link RefreshView} 铺成 MATCH_PARENT 的布局，
 * 各种下拉刷新效果直接继承 RefreshView 并在其中实现即可
 * </p>
 * Created by linxiao on 2017/6/21.
 */
public class PullRefreshLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild {
    private static final String TAG = PullRefreshLayout.class.getSimpleName();
    
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
    
    // 嵌套滑动辅助
    private float mTotalUnconsumed;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private int[] mParentScrollConsumed = new int[2];
    private int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;
    private boolean dragging;
    
    // 下拉刷新动画View
    private RefreshView mRefreshView;
    // 下拉刷新子容器
    private View mTargetView;
    // 插值器，用于回弹动画
    private Interpolator mDecelerateInterpolator;
    // 滑动判断距离
    private float preX;
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
    private int mCurrentTranslationY;
    // 动画缓存值
    private int mFrom;
    // 触发下拉的触摸点Id
    private int mActivePointerId;
    // 是否正在刷新
    private boolean refreshing = false;
    // 是否正在拉动
    private boolean draggingHeader;
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
            int offset = (int) (targetTop - getCurrentMoveDistance());
            moveRefreshHeader(offset, false);
        }
    };
    
    private Animation moveToRefreshAnimation = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mSpinnerFinalOffset;
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = (int) (targetTop - getCurrentMoveDistance());
            moveRefreshHeader(offset, false);
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
        public void onAnimationRepeat(Animation animation) {}
        
        @Override
        public void onAnimationEnd(Animation animation) {
            if (!refreshing) {
                mRefreshView.stopRefreshAnim();
                moveToStartPosition();
            }
            mCurrentTranslationY = getCurrentMoveDistance();
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
            mCurrentTranslationY = getCurrentMoveDistance();
        }
    };
    
    
    public PullRefreshLayout(Context context) {
        super(context);
        init(context, null);
    }
    
    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
        
        setRefreshView(new DefaultRefreshView(context));
        
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
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
    
    /* ------------- implements from NestedScrollingParent ------------- */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !refreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
    
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }
    
    @Override
    public void onStopNestedScroll(View child) {
        mNestedScrollingParentHelper.onStopNestedScroll(child);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
//            if (draggingHeader) {
//                finishSpinner(mTotalUnconsumed);
//            }
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }
    
    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);
        
        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
//            moveSpinner(mTotalUnconsumed); // 嵌套滚动时作下拉刷新等操作处理
        }
    }
    
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
//            moveSpinner((int) mTotalUnconsumed); // 嵌套滚动时作下拉刷新等操作处理
        }
        
        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
//        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
//                && Math.abs(dy - consumed[1]) > 0) {
//            mCircleView.setVisibility(View.GONE);
//        }
        
        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }
    
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }
    
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }
    
    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
    
    /* ------------- implements from NestedScrollingChild ------------- */
    
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }
    
    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }
    
    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }
    
    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }
    
    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }
    
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }
    
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }
    
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }
    
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    
    /* ------------------------------------------------------------- */
    
    private void captureTargetView() {
        if (mTargetView != null || getChildCount() <= 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!child.equals(mRefreshView)) {
                mTargetView = child;
                mTargetView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                );
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
        super.onLayout(changed, l, t, r, b);
        captureTargetView();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setRefreshing(false);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || (canChildScrollUp() && !refreshing) /*|| (dragging && mNestedScrollInProgress)*/) {
            return false;
        }
        if (mInterceptListener != null && !mInterceptListener.onInterceptTouchEvent(ev)) {
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            preX = MotionEvent.obtain(ev).getX();
            dragging = true;
            if (!refreshing) {
                moveRefreshHeader(0, true);
            }
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            draggingHeader = false;
            final float initialMotionY = getMotionY(ev, mActivePointerId);
            if (initialMotionY == -1) {
                return false;
            }
            mInitialMotionY = initialMotionY;
            mInitialOffsetTop = mCurrentTranslationY;
            dispatchTouchDown = false;
            mDragPercent = 0;
            break;
        case MotionEvent.ACTION_MOVE:
            float eventX = ev.getX();
            float xDiff = Math.abs(eventX - preX);
            if (xDiff > mTouchSlop) {
                return false;
            }
            if (mActivePointerId == INVALID_POINTER) {
                return false;
            }
            final float y = getMotionY(ev, mActivePointerId);
            if (y == -1) {
                return false;
            }
            final float yDiff = y - mInitialMotionY;
            if (refreshing) {
                draggingHeader = !(yDiff < 0 && mCurrentTranslationY <= 0);
            }
            else if (yDiff > mTouchSlop) {
                draggingHeader = true;
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            dragging = false;
            draggingHeader = false;
            mActivePointerId = INVALID_POINTER;
            break;
        case MotionEventCompat.ACTION_POINTER_UP:
            onSecondaryPointerUp(ev);
            break;
        }
        
        return draggingHeader;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!draggingHeader) {
            return super.onTouchEvent(event);
        }
        final int action = MotionEventCompat.getActionMasked(event);
        if (!isEnabled() || (canChildScrollUp() && !refreshing)) {
            return false;
        }
        switch (action) {
        case MotionEvent.ACTION_MOVE:
            final float y = event.getY();
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
            moveRefreshHeader(targetY - mCurrentTranslationY, true);
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
            draggingHeader = false;
            finishSpinner(overScrollTop);
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
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrom = mCurrentTranslationY;
                moveToStartAnimation.reset();
                moveToStartAnimation.setDuration(mDurationToStartPosition);
                moveToStartAnimation.setInterpolator(mDecelerateInterpolator);
                moveToStartAnimation.setAnimationListener(mToStartListener);
                mRefreshView.clearAnimation();
                mRefreshView.startAnimation(moveToStartAnimation);
            }
        }, 50);
    }
    
    private void moveToRefreshPosition() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrom = mCurrentTranslationY;
                moveToRefreshAnimation.reset();
                moveToRefreshAnimation.setDuration(mDurationToCorrectPosition);
                moveToRefreshAnimation.setInterpolator(mDecelerateInterpolator);
                moveToRefreshAnimation.setAnimationListener(mRefreshListener);
                mRefreshView.clearAnimation();
                mRefreshView.startAnimation(moveToRefreshAnimation);
            }
        }, 50);
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
    
    private void finishSpinner(float yDiff) {
        if (yDiff > mTotalDragDistance) {
            setRefreshing(true);
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
        } else {
            refreshing = false;
            moveToStartPosition();
        }
    }
    
    private void moveRefreshHeader(int offset, boolean requiresUpdate) {
//        FrameLayout.LayoutParams targetParams = (FrameLayout.LayoutParams) mTargetView.getLayoutParams();
//        targetParams.topMargin += offset;
//        mCurrentTranslationY = targetParams.topMargin;
//        mTargetView.requestLayout();
        
        mTargetView.setTranslationY(mCurrentTranslationY + offset);
        mCurrentTranslationY = getCurrentMoveDistance();
        
        if (requiresUpdate) {
            invalidate();
        }
        mRefreshView.setDragOffset(offset);
    }
    
    public int getCurrentMoveDistance() {
        return (int) mTargetView.getTranslationY();
//        return (int) mCurrentTranslationY;
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
