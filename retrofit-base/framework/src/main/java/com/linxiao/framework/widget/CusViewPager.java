package com.linxiao.framework.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 功能扩展的ViewPager
 * <p>
 * 处理低版本上的嵌套滑动冲突
 * FIXME：普遍的采用重写内层ViewPager onTouchEvent()方法在快速滑动的时候并不生效，因为事件根本没传递到内层就被外层消费
 * 考虑采用外层onInterceptTouchEvent()内加入监听器的方法去处理该问题。
 * 2.
 * </p>
 * Created by linxiao on 2017/2/12.
 */
public class CusViewPager extends ViewPager {
    
    public String TAG;

    private PointF pDown = new PointF();
    private PointF pCurr = new PointF();


    public CusViewPager(Context context) {
        super(context);
    }

    public CusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: ");
        //TODO 需要在这里 判断是否拦截事件，否则在快速滑动的时候事件根本不会传到下级View
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent: ");
        if (getChildCount() <= 1) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            getParent().requestDisallowInterceptTouchEvent(true);
            pDown.x = ev.getX();
            pDown.y = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            pCurr.x = ev.getX();
            pCurr.y = ev.getY();
            if (Math.abs(pCurr.x - pDown.x) < Math.abs(pCurr.y - pDown.y)) {
                Log.d(TAG, "onTouchEvent: not horizontal sliding");
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
            if (pCurr.x > pDown.x) { // slide right
                if (getCurrentItem() == 0) {
                    Log.d(TAG, "onTouchEvent: slide right on first page");
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return super.onTouchEvent(ev);
                }
            }
            else if (pCurr.x < pDown.x) { // slide left
                if (getCurrentItem() == getAdapter().getCount() - 1) {
                    Log.d(TAG, "onTouchEvent: slid left on last page");
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return super.onTouchEvent(ev);
                }
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            getParent().requestDisallowInterceptTouchEvent(false);
            return super.onTouchEvent(ev);
        }
        super.onTouchEvent(ev);
        return true;
//        return super.onTouchEvent(ev);
    }
}
