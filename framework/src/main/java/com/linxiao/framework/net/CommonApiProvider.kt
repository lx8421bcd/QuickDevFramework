package com.linxiao.framework.net

import com.linxiao.framework.net.CommonApiProvider.CommonApi
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 * common retrofit network request method
 *
 *
 * used to handle some special situation that can only request with full url
 * and the structure of response body do not meet the default definition
 * retrofit 常用请求默认定义，用于处理一些只能使用
 *
 *
 * @author linxiao
 * @since 2018/6/11.
 */
object CommonApiProvider : RetrofitApiProvider<CommonApi>() {

    @JvmSuppressWildcards
    interface CommonApi {
        /**
         * 直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @GET
        operator fun get(@Url url: String?): Observable<Response<ResponseBody?>?>?

        /**
         * 直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @GET
        operator fun get(
            @Url url: String?,
            @QueryMap params: Map<String?, Any?>?
        ): Observable<Response<ResponseBody?>?>?

        /**
         * 无参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @POST
        fun post(@Url url: String?): Observable<Response<ResponseBody?>?>?

        /**
         * 带参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @param params 请求数据，将会以form形式包装
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @FormUrlEncoded
        @POST
        fun postForm(
            @Url url: String?,
            @FieldMap params: Map<String?, Any?>?
        ): Observable<Response<ResponseBody?>?>?

        /**
         * 带参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @param params 请求数据，将会以JSON形式包装
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @POST
        fun postJson(
            @Url url: String?,
            @Body params: Map<String?, Any?>?
        ): Observable<Response<ResponseBody?>?>?

        /**
         * 无参数get请求，返回[ApiResponse]对象
         *
         * 必须明确接口返回值为[ApiResponse]声明格式再使用此接口，否则会产生解析异常
         * @param url 请求完整url
         * @return RxJava Observable with ApiResponse
         */
        @GET
        fun apiGet(@Url url: String?): Observable<ApiResponse?>?

        /**
         * 带参数get请求，返回[ApiResponse]对象
         *
         * 必须明确接口返回值为[ApiResponse]声明格式再使用此接口，否则会产生解析异常
         * @param url 请求完整url
         * @param queryMap 请求参数表
         * @return RxJava Observable with ApiResponse
         */
        @GET
        fun apiGet(
            @Url url: String?,
            @QueryMap queryMap: Map<String?, Any?>?
        ): Observable<ApiResponse?>?

        /**
         * 无参数post请求，返回[ApiResponse]对象
         *
         * 必须明确接口返回值为[ApiResponse]声明格式再使用此接口，否则会产生解析异常
         * @param url 请求完整url
         * @return RxJava Observable with ApiResponse
         */
        @POST
        fun apiPost(@Url url: String?): Observable<ApiResponse?>?

        /**
         * 带参数post请求，返回[ApiResponse]对象
         *
         * 必须明确接口返回值为[ApiResponse]声明格式再使用此接口，否则会产生解析异常
         * @param url 请求完整url
         * @param formMap 请求参数表，将会以form形式包装
         * @return RxJava Observable with ApiResponse
         */
        @FormUrlEncoded
        @POST
        fun apiPostForm(
            @Url url: String?,
            @FieldMap formMap: Map<String?, Any?>?
        ): Observable<ApiResponse?>?

        /**
         * 带参数post请求，返回[ApiResponse]对象
         *
         * 必须明确接口返回值为[ApiResponse]声明格式再使用此接口，否则会产生解析异常
         * @param url 请求完整url
         * @param requestBody 请求参数表，将会以JSON形式包装
         * @return RxJava Observable with ApiResponse
         */
        @POST
        fun apiPostJson(
            @Url url: String?,
            @Body requestBody: Map<String?, Any?>?
        ): Observable<ApiResponse?>?
    }

    override fun provideApiBaseUrl(): String {
        return "useless.host"
    }

    override fun provideApiClass(): Class<CommonApi> {
        return CommonApi::class.java
    }
}
