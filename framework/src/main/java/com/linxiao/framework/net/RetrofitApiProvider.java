package com.linxiao.framework.net;

import android.text.TextUtils;

import java.lang.reflect.ParameterizedType;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Retrofit management class
 * <p>
 * provides the default retrofit config method, default OkHttpClient Builder
 * and default {@link HttpInfoCatchInterceptor} implementation.
 * see code for more details.
 * </p>
 *
 * Created by linxiao on 2016-11-27.
 */
public abstract class RetrofitApiProvider<T> {

    private OkHttpClient cachedClient;
    private String baseUrl;
    private T api;

    public T getApi() {
        if (api == null || cachedClient != getOkHttpClient() ||
                !TextUtils.equals(baseUrl, getApiBaseUrl())
        ) {
            buildClientApi();
        }
        return api;
    }

    protected abstract String getApiBaseUrl();

    @SuppressWarnings("unchecked")
    protected  Class<T> getApiClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected OkHttpClient getOkHttpClient() {
        return GlobalOkHttpClientHelper.INSTANCE.getDefaultClient();
    }

    protected Converter.Factory getConverterFactory() {
        return ApiConverterFactory.create();
    }

    protected void buildClientApi() {
        cachedClient = getOkHttpClient();
        baseUrl = getApiBaseUrl();
        Retrofit.Builder builder = new Retrofit.Builder();
        if (!TextUtils.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        builder.baseUrl(baseUrl);
        builder.client(cachedClient);
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.addConverterFactory(getConverterFactory());
        api = builder.build().create(getApiClass());
    }
}
