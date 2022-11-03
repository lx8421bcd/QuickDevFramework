package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.linxiao.framework.R;
import com.linxiao.framework.common.ErrorMessageUtil;

/**
 * 自定义加载布局
 * <p>可以整合加载中, 加载失败, 加载为空几种状态的界面,简化布局, 默认显示内容布局</p>
 *
 * Created by linxiao on 2016-05-15.
 */
public class LoadingView extends FrameLayout {

    private static final String TAG = LoadingView.class.getSimpleName();

    private static final long DEFAULT_FADE_IN_DURATION = 300;

    @LayoutRes
    private static int defaultEmptyViewRes = 0;

    @LayoutRes
    private static int defaultLoadingViewRes = 0;

    @LayoutRes
    private static int defaultErrorViewRes = 0;

    public static void setDefaultEmptyViewRes(@LayoutRes int defaultEmptyViewRes) {
        LoadingView.defaultEmptyViewRes = defaultEmptyViewRes;
    }

    public static void setDefaultLoadingViewRes(@LayoutRes int defaultLoadingViewRes) {
        LoadingView.defaultLoadingViewRes = defaultLoadingViewRes;
    }

    public static void setDefaultErrorViewRes(@LayoutRes int defaultErrorViewRes) {
        LoadingView.defaultErrorViewRes = defaultErrorViewRes;
    }

    private View emptyView;
    private View loadingView;
    private View errorView;

    private long fadeInDuration = DEFAULT_FADE_IN_DURATION;

    public LoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.loading_view, this, true);
        setBackgroundColor(Color.WHITE);
        emptyView = findViewById(R.id.empty_view);
        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            int resEmptyView = typedArray.getResourceId(R.styleable.LoadingView_l_emptyView, defaultEmptyViewRes);
            if (resEmptyView != 0) {
                emptyView = LayoutInflater.from(context).inflate(resEmptyView, this, false);
                setEmptyView(emptyView);
            }
            int resLoadingView = typedArray.getResourceId(R.styleable.LoadingView_l_loadingView, defaultLoadingViewRes);
            if (resLoadingView != 0) {
                loadingView = LayoutInflater.from(context).inflate(resLoadingView, this, false);
                setLoadingView(loadingView);
            }
            int resErrorView = typedArray.getResourceId(R.styleable.LoadingView_l_errorView, defaultErrorViewRes);
            if (resErrorView != 0) {
                errorView = LayoutInflater.from(context).inflate(resErrorView, this, false);
                setErrorView(errorView);
            }
            typedArray.recycle();
        }
        showView(loadingView);
        setOnClickListener(v -> {});
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
        this.addView(this.emptyView);
        this.emptyView.setVisibility(View.GONE);
    }

    public void setLoadingView(View loadingView) {
        this.removeView(this.loadingView);
        this.loadingView = loadingView;
        this.addView(this.loadingView);
        this.loadingView.setVisibility(View.GONE);
    }

    public void setErrorView(View errorView) {
        this.removeView(this.errorView);
        this.errorView = errorView;
        this.addView(this.errorView);
        this.errorView.setVisibility(View.GONE);
    }

    public void showEmptyView() {
        showEmptyView(false);
    }

    public void showEmptyView(boolean isFadeIn) {
        showView(emptyView);
        if (emptyView.getVisibility() == View.VISIBLE) {
            if (isFadeIn) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(fadeInDuration);
                emptyView.startAnimation(fadeIn);
            }
        }
    }

    public void showLoadingView() {
        showView(loadingView);
    }

    public void showErrorView() {
        showErrorView("");
    }

    public void showErrorView(Throwable e) {
        showErrorView(ErrorMessageUtil.getMessageString(e));
    }

    public void showErrorView(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            TextView tvErrorDesc = errorView.findViewById(R.id.tv_desc);
            if (tvErrorDesc != null) {
                tvErrorDesc.setText(desc);
            }
        }
        showView(errorView);
    }

    public void showContentView() {
        showContentView(false);
    }

    public void showContentView(boolean isFadeIn) {
        if (getVisibility() == View.GONE) {
            return;
        }
        if (isFadeIn) {
            Animation fadeIn = new AlphaAnimation(1, 0);
            fadeIn.setDuration(fadeInDuration);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            startAnimation(fadeIn);
        }
        else {
            setVisibility(GONE);
        }
    }

    public void setContentFadeInDuration(long duration) {
        if (fadeInDuration > 0) {
            fadeInDuration = duration;
        }
    }

    protected void showView(View v) {
        setVisibility(VISIBLE);
        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChildAt(i).setVisibility(View.GONE);
        }
        v.setVisibility(View.VISIBLE);
    }



}
