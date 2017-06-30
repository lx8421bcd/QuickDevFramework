package com.linxiao.framework.widget.pullrefresh;

import android.content.Context;
import android.view.Gravity;
import android.widget.ProgressBar;


/**
 * Material 圆圈式下拉刷新布局
 * Created by linxiao on 2017/6/21.
 */

public class DefaultRefreshView extends RefreshView {
    
    private int refreshViewSize = dp2px(28);
    
    ProgressBar mRefreshView;
    
    public DefaultRefreshView(Context context) {
        super(context);
        setMaxDragDistance(dp2px(40));
        
        mRefreshView = new ProgressBar(context);
        LayoutParams params = new LayoutParams(refreshViewSize, refreshViewSize);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, -refreshViewSize, 0, 0 );
        addView(mRefreshView, params);
    
        mRefreshView.getIndeterminateDrawable().setColorFilter(0xFFFF0000,
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    
    @Override
    public void setDragOffset(int offset) {
        LayoutParams params = (LayoutParams) mRefreshView.getLayoutParams();
        params.topMargin += offset;
        requestLayout();
    }
    
    @Override
    public void startRefreshAnim() {
//        mRefreshView.setProgress();
    }
    
    @Override
    public void stopRefreshAnim() {
        
    }
    
    @Override
    public boolean isRunningAnim() {
        return false;
    }
}
