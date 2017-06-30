package com.linxiao.framework.widget.pullrefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

/**
 * 下拉刷新HeaderView
 *
 * @author linxiao
 */

public abstract class RefreshView extends FrameLayout{

    /**
     * 最大下拉距离
     * */
    private int maxDragDistance = dp2px(80);
    
    public RefreshView(Context context) {
        super(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setDragOffset(int offset);
    
    public abstract void startRefreshAnim();
    
    public abstract void stopRefreshAnim();
    
    public abstract boolean isRunningAnim();
    
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics()
        );
    }
    /**
     * 设置最大下拉距离
     * */
    public void setMaxDragDistance(int distance) {
        maxDragDistance = distance;
    }
    
    public int getMaxDragDistance() {
        return maxDragDistance;
    }
    
}
