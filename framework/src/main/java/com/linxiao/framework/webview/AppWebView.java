package com.linxiao.framework.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用框架内基础WebView
 * <p> class description </p>
 *
 * @author linxiao
 * @since  2018-06-01.
 */
public class AppWebView extends WebView {

    private static final String TAG = AppWebView.class.getSimpleName();
    // 默认JS Interface名称
    public static final String DEFAULT_JS_INTERFACE = "WebViewJavascriptBridge";
    // 默认H5暴露给native的回调方法，需与前端协调
    public static final String DEFAULT_NATIVE_TO_JS_METHOD = "invokeFromNative";

    public interface OnScrollChangedListener {

        void onScrollChanged(WebView webView, int scrolledX, int scrolledY, int dx, int dy);
    }

    /**
     * 通用JTN回调处理
     * */
    public interface JSCallHandler {

        /**
         * JTN事件处理方法
         * @param method 方法
         * @param body 事件数据对象
         *
         * @return handleDefault, 事件是否执行WebView内部的处理，
         *         false: 不执行WebView内的事件处理流程
         *         true: 执行WebView内部的处理流程
         */
        boolean onJSCall(AppWebView webView, String method, String body);
    }

    /**
     * WebView注入对象
     */
    public static class JSInterface {

        private final AppWebView webView;

        public JSInterface(AppWebView webView) {
            this.webView = webView;
        }

        /**
         * JS调用:调用Android方法
         *
         * @param method 方法名
         * @param body   参数
         */
        @JavascriptInterface
        public void invokeMethod(String method, String body) {
            onInvokeJSMethod(method, body);
        }

        public void onInvokeJSMethod(String method, String body) {
            Log.i(TAG, "js invoke android method:messageObject=" + body);
            webView.onInvokeJSMethod(method, body);
        }
    }

    private static final List<JSCallHandler> globalJSCallHandlers = new ArrayList<>();
    private static String defaultUserAgent;

    private String defaultNativeToJsMethod = "";
    private JSCallHandler jsCallHandler;

    private int scrolledX;
    private int scrolledY;
    private OnScrollChangedListener onScrollChangedListener;

    public static void setDefaultUserAgent(String defaultUserAgent) {
        AppWebView.defaultUserAgent = defaultUserAgent;
    }

    public static void addGlobalJSCallHandler(JSCallHandler handler) {
        if (!globalJSCallHandlers.contains(handler)) {
            globalJSCallHandlers.add(handler);
        }
    }

    public static void removeGlobalJSCallHandler(JSCallHandler handler) {
        globalJSCallHandlers.remove(handler);
    }

    public AppWebView(Context context) {
        super(context);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        if (!isInEditMode()) {
//        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
            getSettings().setSupportZoom(false);
            getSettings().setBuiltInZoomControls(false);
            getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            getSettings().setDomStorageEnabled(true);
            getSettings().setDatabaseEnabled(true);
            if (!TextUtils.isEmpty(defaultUserAgent)) {
                getSettings().setUserAgentString(defaultUserAgent);
            }
            addJSInterface(DEFAULT_JS_INTERFACE, new JSInterface(this));
        }
    }


    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public <T extends JSInterface> void addJSInterface(String name, T jsInterface) {
        getSettings().setJavaScriptEnabled(true);
        removeJavascriptInterface(name);
        addJavascriptInterface(jsInterface, name);
    }

    public void setJsCallHandler(JSCallHandler jsCallHandler) {
        this.jsCallHandler = jsCallHandler;
    }

    /**
     * 调用JS函数
     * @param params js函数入参
     */
    public void invokeJSMethod(String params) {
        invokeJSMethod(defaultNativeToJsMethod, params);
    }

    /**
     * 调用JS函数
     * @param method js暴露的函数名
     * @param params js函数入参
     */
    public void invokeJSMethod(String method, String params) {
        method = TextUtils.isEmpty(method) ? defaultNativeToJsMethod : method;
        if (TextUtils.isEmpty(method)) {
            method = DEFAULT_NATIVE_TO_JS_METHOD;
        }
        params = TextUtils.isEmpty(params) ? "" : params;
        String invokeString = String.format("javascript:%s(%s)", method, params);
        this.post(() -> loadUrl(invokeString));
    }

    public void setDefaultNativeToJsMethod(String defaultNativeToJsMethod) {
        this.defaultNativeToJsMethod = defaultNativeToJsMethod;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.onScrollChangedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int dl = l - oldl;
        int dt = t - oldt;
        scrolledX += dl;
        scrolledY += dt;
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(this, scrolledX, scrolledY, dl, dt);
        }
    }

    private void onInvokeJSMethod(String method, String body) {
        boolean consumed;
        if (jsCallHandler != null) {
            consumed = jsCallHandler.onJSCall(this, method, body);
            if (consumed) {
                return;
            }
        }
        for (JSCallHandler handler : globalJSCallHandlers) {
            consumed = handler.onJSCall(this, method, body);
            if (consumed) {
                return;
            }
        }

        Log.i(TAG, "onInvokeJSMethod: receive call from javascript but no method handled, method = " + method);
    }

}
