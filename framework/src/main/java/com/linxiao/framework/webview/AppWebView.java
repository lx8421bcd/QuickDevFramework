package com.linxiao.framework.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

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

    public interface GlobalJSCallHandler extends JSCallHandler {

        boolean onOwnerActivityResult(AppWebView webView, int requestCode, int resultCode, Intent data);

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

    private WebViewClient mWebViewClient;
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

    @Override
    public void setWebViewClient(WebViewClient client) {
        mWebViewClient = client;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        super.setWebViewClient(new WebViewClientWrapper() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
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

    /**
     * 用于触发{@link GlobalJSCallHandler}的onOwnerActivityResult回调
     * <p>在对应Activity的onActivityResult()方法中调用此方法</p>
     */
    public void onOwnerActivityResult(int requestCode, int resultCode, Intent data) {
        for (JSCallHandler handler : globalJSCallHandlers) {
            if (handler instanceof GlobalJSCallHandler) {
                boolean consumed = ((GlobalJSCallHandler) handler)
                        .onOwnerActivityResult(this, requestCode, resultCode, data);
                if (consumed) {
                    return;
                }
            }
        }
    }

    /**
     * 由ChildWebViewClient代理对外的WebViewClient设置，此类用于处理WebView内部的回调
     */
    private class WebViewClientWrapper extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mWebViewClient != null) {
                return mWebViewClient.shouldOverrideUrlLoading(view, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mWebViewClient != null) {
                mWebViewClient.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mWebViewClient != null) {
                mWebViewClient.onPageFinished(view, url);
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (mWebViewClient != null) {
                mWebViewClient.onLoadResource(view, url);
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            if (mWebViewClient != null) {
                mWebViewClient.onPageCommitVisible(view, url);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mWebViewClient != null) {
                return mWebViewClient.shouldInterceptRequest(view, url);
            }
            return super.shouldInterceptRequest(view, url);
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (mWebViewClient != null) {
                return mWebViewClient.shouldInterceptRequest(view, request);
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
            if (mWebViewClient != null) {
                mWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedError(view, request, error);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            super.onFormResubmission(view, dontResend, resend);
            if (mWebViewClient != null) {
                mWebViewClient.onFormResubmission(view, dontResend, resend);
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (mWebViewClient != null) {
                mWebViewClient.doUpdateVisitedHistory(view, url, isReload);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedSslError(view, handler, error);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            super.onReceivedClientCertRequest(view, request);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedClientCertRequest(view, request);
            }
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            if (mWebViewClient != null) {
                return mWebViewClient.shouldOverrideKeyEvent(view, event);
            }
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            super.onUnhandledKeyEvent(view, event);
            if (mWebViewClient != null) {
                mWebViewClient.onUnhandledKeyEvent(view, event);
            }
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            if (mWebViewClient != null) {
                mWebViewClient.onScaleChanged(view, oldScale, newScale);
            }
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            super.onReceivedLoginRequest(view, realm, account, args);
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedLoginRequest(view, realm, account, args);
            }
        }
    }

}
