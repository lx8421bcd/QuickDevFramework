package com.linxiao.framework.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.linxiao.framework.R

/**
 * 自定义加载布局
 *
 * 可以整合加载中, 加载失败, 加载为空几种状态的界面,简化布局, 默认显示内容布局
 *
 * @author lx8421bcd
 * @since 2016-05-15
 */
class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    fun interface ExceptionParser {

        fun parse(e: Throwable): String

    }

    companion object {
        private const val DEFAULT_FADE_IN_DURATION: Long = 300
        var defaultExceptionParser: ExceptionParser = ExceptionParser {
            return@ExceptionParser it.message ?: ""
        }
        @LayoutRes
        var defaultEmptyViewRes = 0
        @LayoutRes
        var defaultLoadingViewRes = 0
        @LayoutRes
        var defaultErrorViewRes = 0
    }

    private var emptyView: View = findViewById(R.id.empty_view)
        set(value) {
            removeView(field)
            field = value
            this.addView(field)
            field.visibility = GONE
        }
    private var loadingView: View = findViewById(R.id.loading_view)
        set(value) {
            removeView(field)
            field = value
            this.addView(field)
            field.visibility = GONE
        }
    private var errorView: View = findViewById(R.id.error_view)
        set(value) {
            removeView(field)
            field = value
            this.addView(field)
            field.visibility = GONE
        }
    private var fadeInDuration = DEFAULT_FADE_IN_DURATION

    var exceptionParser: ExceptionParser = defaultExceptionParser

    init {
        LayoutInflater.from(context).inflate(R.layout.loading_view, this, true)
        setBackgroundColor(Color.WHITE)
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
            val resEmptyView = typedArray.getResourceId(R.styleable.LoadingView_l_emptyView, defaultEmptyViewRes)
            if (resEmptyView != 0) {
                emptyView = LayoutInflater.from(context).inflate(resEmptyView, this, false)
            }
            val resLoadingView = typedArray.getResourceId(R.styleable.LoadingView_l_loadingView, defaultLoadingViewRes)
            if (resLoadingView != 0) {
                loadingView = LayoutInflater.from(context).inflate(resLoadingView, this, false)
            }
            val resErrorView = typedArray.getResourceId(R.styleable.LoadingView_l_errorView, defaultErrorViewRes)
            if (resErrorView != 0) {
                errorView = LayoutInflater.from(context).inflate(resErrorView, this, false)
            }
            typedArray.recycle()
        }
        showView(loadingView)
        setOnClickListener { _: View? -> }
    }

    @JvmOverloads
    fun showEmptyView(isFadeIn: Boolean = false) {
        showView(emptyView)
        if (emptyView.visibility == VISIBLE) {
            if (isFadeIn) {
                val fadeIn: Animation = AlphaAnimation(0f, 1f)
                fadeIn.setDuration(fadeInDuration)
                emptyView.startAnimation(fadeIn)
            }
        }
    }

    fun startLoading() {
        showLoadingView()
    }

    @JvmOverloads
    fun endLoading(
        empty: Boolean = false,
    ) {
        if (empty) {
            showEmptyView(true)
        }
        else {
            showContentView(true)
        }
    }

    fun showLoadingView() {
        showView(loadingView)
    }

    fun showErrorView(e: Throwable) {
        showErrorView(exceptionParser.parse(e))
    }

    @JvmOverloads
    fun showErrorView(desc: String = "") {
        errorView.findViewById<TextView>(R.id.tv_desc)?.apply {
            text = desc
        }
        showView(errorView)
    }

    @JvmOverloads
    fun showContentView(isFadeIn: Boolean = false) {
        if (visibility == GONE) {
            return
        }
        if (isFadeIn) {
            val fadeIn: Animation = AlphaAnimation(1f, 0f)
            fadeIn.setDuration(fadeInDuration)
            fadeIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    visibility = GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            startAnimation(fadeIn)
        } else {
            visibility = GONE
        }
    }

    fun setContentFadeInDuration(duration: Long) {
        if (fadeInDuration > 0) {
            fadeInDuration = duration
        }
    }

    protected fun showView(v: View?) {
        visibility = VISIBLE
        for (i in 0 until this.childCount) {
            getChildAt(i).visibility = GONE
        }
        v!!.visibility = VISIBLE
    }
}
