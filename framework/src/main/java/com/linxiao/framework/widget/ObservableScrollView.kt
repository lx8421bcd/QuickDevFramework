package com.linxiao.framework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 设置滚动距离监听的ScrollView
 * <p>通过设置OnScrollChangedListener监听滚动距离</p>
 *
 * @author linxiao
 * @version 1.0
 */
public class ObservableScrollView extends ScrollView {

    public interface OnScrollChangedListener {

        void onScrollChanged(ScrollView scrollView, int scrolledX, int scrolledY, int dx, int dy);

    }

    private OnScrollChangedListener listener;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int dl = l - oldl;
        int dt = t - oldt;;
        if(listener != null) {
            listener.onScrollChanged(this, getScrollX(), getScrollY(), dl, dt);
        }

    }
}
