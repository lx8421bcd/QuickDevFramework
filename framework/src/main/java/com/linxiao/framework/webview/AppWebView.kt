package com.linxiao.framework.webview

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

private val TAG = AppWebView::class.java.getSimpleName()

/**
 * 应用框架内基础WebView
 *
 *  class description
 *
 * @author linxiao
 * @since  2018-06-01.
 */
open class AppWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    interface OnScrollChangedListener {
        fun onScrollChanged(webView: WebView?, scrolledX: Int, scrolledY: Int, dx: Int, dy: Int)
    }

    /**
     * 通用JTN回调处理
     */
    interface JSCallHandler {
        /**
         * JTN事件处理方法
         * @param method 方法
         * @param body 事件数据对象
         *
         * @return handleDefault, 事件是否执行WebView内部的处理，
         * false: 不执行WebView内的事件处理流程
         * true: 执行WebView内部的处理流程
         */
        fun onJSCall(webView: AppWebView?, method: String?, body: String?): Boolean
    }

    /**
     * WebView注入对象
     */
    open class JSInterface(private val webView: AppWebView) {
        /**
         * JS调用:调用Android方法
         *
         * @param method 方法名
         * @param body   参数
         */
        @JavascriptInterface
        fun invokeMethod(method: String, body: String) {
            onInvokeJSMethod(method, body)
        }

        fun onInvokeJSMethod(method: String, body: String) {
            Log.i(TAG, "js invoke android method:messageObject=$body")
            webView.onInvokeJSMethod(method, body)
        }
    }

    companion object {

        // 默认JS Interface名称
        const val DEFAULT_JS_INTERFACE = "WebViewJavascriptBridge"

        // 默认H5暴露给native的回调方法，需与前端协调
        const val DEFAULT_NATIVE_TO_JS_METHOD = "invokeFromNative"
        private val globalJSCallHandlers: MutableList<JSCallHandler> = ArrayList()
        private var defaultUserAgent: String? = null
        fun setDefaultUserAgent(defaultUserAgent: String?) {
            Companion.defaultUserAgent = defaultUserAgent
        }

        fun addGlobalJSCallHandler(handler: JSCallHandler) {
            if (!globalJSCallHandlers.contains(handler)) {
                globalJSCallHandlers.add(handler)
            }
        }

        fun removeGlobalJSCallHandler(handler: JSCallHandler) {
            globalJSCallHandlers.remove(handler)
        }
    }

    private var defaultNativeToJsMethod = ""
    private var jsCallHandler: JSCallHandler? = null
    private var scrolledX = 0
    private var scrolledY = 0
    private var onScrollChangedListener: OnScrollChangedListener? = null

    init {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        if (!isInEditMode) {
//        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            getSettings().allowFileAccess = true // 设置允许访问文件数据
            getSettings().setSupportZoom(false)
            getSettings().builtInZoomControls = false
            getSettings().javaScriptCanOpenWindowsAutomatically = true
            getSettings().domStorageEnabled = true
            getSettings().databaseEnabled = true
            if (!TextUtils.isEmpty(defaultUserAgent)) {
                getSettings().userAgentString = defaultUserAgent
            }
            addJSInterface(DEFAULT_JS_INTERFACE, JSInterface(this))
        }
    }

    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    fun <T : JSInterface> addJSInterface(name: String, jsInterface: T) {
        getSettings().javaScriptEnabled = true
        removeJavascriptInterface(name)
        addJavascriptInterface(jsInterface, name)
    }

    fun setJsCallHandler(jsCallHandler: JSCallHandler?) {
        this.jsCallHandler = jsCallHandler
    }

    /**
     * 调用JS函数
     * @param params js函数入参
     */
    fun invokeJSMethod(params: String) {
        invokeJSMethod(defaultNativeToJsMethod, params)
    }

    /**
     * 调用JS函数
     * @param method js暴露的函数名
     * @param params js函数入参
     */
    fun invokeJSMethod(method: String, params: String) {
        post { loadUrl("javascript:${method}(${params})") }
    }

    fun setDefaultNativeToJsMethod(defaultNativeToJsMethod: String) {
        this.defaultNativeToJsMethod = defaultNativeToJsMethod
    }

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        onScrollChangedListener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val dl = l - oldl
        val dt = t - oldt
        scrolledX += dl
        scrolledY += dt
        if (onScrollChangedListener != null) {
            onScrollChangedListener!!.onScrollChanged(this, scrolledX, scrolledY, dl, dt)
        }
    }

    private fun onInvokeJSMethod(method: String, body: String) {
        var consumed: Boolean
        if (jsCallHandler != null) {
            consumed = jsCallHandler!!.onJSCall(this, method, body)
            if (consumed) {
                return
            }
        }
        for (handler in globalJSCallHandlers) {
            consumed = handler.onJSCall(this, method, body)
            if (consumed) {
                return
            }
        }
        Log.i(TAG, "onInvokeJSMethod: receive call from javascript but no method handled, method = $method")
    }
}
