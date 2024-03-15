package com.linxiao.framework.net

import android.text.TextUtils
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.ParameterizedType

/**
 * Retrofit management class
 *
 *
 * provides the default retrofit config method, default OkHttpClient Builder
 * and default [HttpInfoCatchInterceptor] implementation.
 * see code for more details.
 *
 * @author lx8421bcd
 * @since 2016-11-27.
 */
@Suppress("UNCHECKED_CAST")
abstract class RetrofitApiProvider<T> {

    private var baseUrl: String = ""
    private var api: T = buildClientApi()
        get() {
            if (TextUtils.equals(baseUrl, provideApiBaseUrl())) {
                field = buildClientApi()
            }
            return field
        }

    protected abstract fun provideApiBaseUrl(): String

    protected open fun provideApiClass(): Class<T> {
        return (javaClass.getGenericSuperclass() as ParameterizedType)
            .actualTypeArguments[0] as Class<T>
    }
    protected open fun provideOkHttpClient(): OkHttpClient {
        return GlobalOkHttpClientHelper.defaultClient
    }
    protected open fun provideConverterFactory(): Converter.Factory {
        return ApiConverterFactory.create()
    }

    protected fun buildClientApi(): T {
        baseUrl = provideApiBaseUrl()
        val builder = Retrofit.Builder()
        if (!TextUtils.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
        builder.baseUrl(baseUrl)
        builder.client(provideOkHttpClient())
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        builder.addConverterFactory(provideConverterFactory())
        return builder.build().create(provideApiClass())
    }
}
