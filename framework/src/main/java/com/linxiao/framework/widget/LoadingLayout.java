package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.linxiao.framework.R;

/**
 * 自定义加载布局
 * <p>可以整合加载中, 加载失败, 加载为空几种状态的界面,简化布局, 默认显示内容布局</p>
 *
 * Created by linxiao on 2016-05-15.
 */
public class LoadingLayout extends FrameLayout {

    private static final String TAG = LoadingLayout.class.getSimpleName();

    private static final long DEFAULT_FADE_IN_DURATION = 300;

    private static final int INDEX_EMPTY = 0;
    private static final int INDEX_LOADING = 1;
    private static final int INDEX_ERROR = 2;
    private static final int INDEX_CONTENT = 3;

    private View emptyView;
    private View loadingView;
    private View errorView;

    private long fadeInDuration = DEFAULT_FADE_IN_DURATION;

    public LoadingLayout(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        int resEmptyView =  R.layout.empty_view;
        int resLoadingView = R.layout.loading_view;
        int resErrorView = R.layout.error_view;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout);
            resEmptyView = typedArray.getResourceId(R.styleable.LoadingLayout_l_emptyView, R.layout.empty_view);
            resLoadingView = typedArray.getResourceId(R.styleable.LoadingLayout_l_loadingView, R.layout.loading_view);
            resErrorView = typedArray.getResourceId(R.styleable.LoadingLayout_l_errorView, R.layout.error_view);
            typedArray.recycle();
        }
        emptyView = LayoutInflater.from(context).inflate(resEmptyView, this, false);
        loadingView = LayoutInflater.from(context).inflate(resLoadingView, this, false);
        errorView = LayoutInflater.from(context).inflate(resErrorView, this, false);

        setEmptyView(emptyView);
        setLoadingView(loadingView);
        setErrorView(errorView);
        showView(INDEX_CONTENT);
    }

    public View getEmptyView() {
        return emptyView;
    }

    public View getLoadingView() {
        return loadingView;
    }

    public View getErrorView() {
        return errorView;
    }

    public void setEmptyView(View emptyView) {
        this.removeView(this.emptyView);
        this.emptyView = emptyView;
        this.addView(this.emptyView, INDEX_EMPTY);
        this.emptyView.setVisibility(View.GONE);
        if (!this.emptyView.isClickable()) {
            this.emptyView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {}   //如果本身没有设置监听则设置一个空的监听来防止误触
            });
        }
    }

    public void setLoadingView(View loadingView) {
        this.removeView(this.loadingView);
        this.loadingView = loadingView;
        this.addView(this.loadingView, INDEX_LOADING);
        this.loadingView.setVisibility(View.GONE);
        if (!this.loadingView.isClickable()) {
            this.loadingView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {}
            });
        }
    }

    public void setErrorView(View errorView) {
        this.removeView(this.errorView);
        this.errorView = errorView;
        this.addView(this.errorView, INDEX_ERROR);
        this.errorView.setVisibility(View.GONE);
        if (!this.errorView.isClickable()) {
            this.errorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {}
            });
        }
    }

    public void showEmptyView() {
        showEmptyView(false);
    }

    public void showEmptyView(boolean isFadeIn) {
        showView(INDEX_EMPTY);
        View emptyView = this.getChildAt(INDEX_EMPTY);
        if (emptyView == null ) {
            Log.e(TAG, "empty view is null, custom view has error, check source code");
            return;
        }
        if (emptyView.getVisibility() == View.VISIBLE) {
            if (isFadeIn) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(fadeInDuration);
                emptyView.startAnimation(fadeIn);
            }
        }
    }

    public void showLoadingView() {
        showView(INDEX_LOADING);
    }

    public void showErrorView() {
        showView(INDEX_ERROR);
    }

    public void showContentView() {
        showContentView(false);
    }

    public void showContentView(boolean isFadeIn) {
        showView(INDEX_CONTENT);
        View contentView = this.getChildAt(INDEX_CONTENT);
        if (contentView == null ) {
            Log.e(TAG, "content view is null !");
            return;
        }
        if (contentView.getVisibility() == View.VISIBLE) {
            if (isFadeIn) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(fadeInDuration);
                contentView.startAnimation(fadeIn);
            }
        }
    }

    public void setContentFadeInDuration(long duration) {
        if (fadeInDuration > 0) {
            fadeInDuration = duration;
        }
        else {
            Log.e(TAG, "set fade in duration illegal: small than 0 !");
        }

    }

    protected void showView(int index) {
        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChildAt(i).setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }



}
