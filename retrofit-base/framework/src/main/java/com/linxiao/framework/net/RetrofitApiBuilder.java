package com.linxiao.framework.net;

import android.support.annotation.NonNull;
import android.support.v4.BuildConfig;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit构建类
 * Created by LinXiao on 2016-12-31.
 */
public class RetrofitApiBuilder {

    private Retrofit.Builder mRetrofitBuilder;
    private OkHttpClient.Builder okHttpClientBuilder;
    private Map<String, String> universalHeaders;

    private CookieMode cookieMode;
    private boolean hasDefaultConvertFactory;

    public RetrofitApiBuilder() {
        mRetrofitBuilder = new Retrofit.Builder();
        okHttpClientBuilder = new OkHttpClient.Builder();
        universalHeaders = new ArrayMap<>();
    }

    /**
     * 设置服务端地址
     * */
    public RetrofitApiBuilder setServerUrl(String serverUrl) {
        mRetrofitBuilder.baseUrl(serverUrl);
        return this;
    }

    /**
     * 配置请求通用Headers
     * @param name Header 名字
     * @param value Header 值
     * */
    public RetrofitApiBuilder addHeader(String name, String value) {
        universalHeaders.put(name, value);
        return this;
    }

    /**
     * 设置Cookie管理模式
     * */
    public RetrofitApiBuilder setCookieMode(CookieMode mode) {
        cookieMode = mode;
        return this;
    }

    /**
     * 添加Https支持
     * TODO: 确定Https支持的必须参数
     * */
    public RetrofitApiBuilder addHttpsSupport(int[] certificates, String[] hostUrls) {
//            okHttpClientBuilder.socketFactory(getSSLSocketFactory(certificates));
//            okHttpClientBuilder.hostnameVerifier(getHostnameVerifier(hostUrls));
        return this;
    }

    /**
     * 添加 CallAdapterFactory
     * */
    @NonNull
    public RetrofitApiBuilder addCallAdapterFactory(CallAdapter.Factory factory) {
        mRetrofitBuilder.addCallAdapterFactory(factory);
        return this;
    }

    /**
     * 添加 ConvertFactory;
     * */
    @NonNull
    public RetrofitApiBuilder addConvertFactory(Converter.Factory factory) {
        mRetrofitBuilder.addConverterFactory(factory);
        hasDefaultConvertFactory = false;
        return this;
    }


    public <T> T build(Class<T> clazzClientApi) {
        //基础拦截器，
        Interceptor configInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();

                for (String key : universalHeaders.keySet()) {
                    builder.addHeader(key, universalHeaders.get(key));
                }
                /* Cookie检查，在Retrofit的请求接口加上"@Header("Set-Cookie")"注解
                去动态选择是否为当前接口添加cookie */
                switch (cookieMode) {
                case NO_COOKIE:
                    break;
                case ADD_BY_ANNOATION:
                    if (chain.request().headers().get("Set-Cookie") != null) {
                        builder.removeHeader("Set-Cookie");
                        if (!TextUtils.isEmpty(SessionManager.getSession())) {
                            builder.header("Set-Cookie", SessionManager.getSession());
                        }
                    }
                    break;
                case ADD_TO_ALL:
                    if (!TextUtils.isEmpty(SessionManager.getSession())) {
                        builder.header("Set-Cookie", SessionManager.getSession());
                    }
                    break;
                default:
                    break;
                }

                Request request = builder.build();

                if (BuildConfig.DEBUG) {
                    Log.d("TAG", "request url : " + request.url());
                }
                return chain.proceed(request);
            }
        };
        Interceptor responseInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                //存入Session
                if (response.header("Set-Cookie") != null) {
                    SessionManager.setSession(response.header("Set-Cookie"));
                }
                //刷新API调用时间
                SessionManager.setLastApiCallTime(System.currentTimeMillis());

                return response;
            }
        };
        okHttpClientBuilder.addInterceptor(configInterceptor);
        okHttpClientBuilder.addInterceptor(responseInterceptor);
        mRetrofitBuilder.client(okHttpClientBuilder.build());

        if (!hasDefaultConvertFactory) {
            mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        }

        return mRetrofitBuilder.build().create(clazzClientApi);
    }

}
