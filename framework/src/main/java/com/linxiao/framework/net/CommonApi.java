package com.linxiao.framework.net;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * common retrofit network request method
 * <p>
 * used to handle some special situation that can only request with full url
 * and the structure of response body do not meet the default definition
 *
 * retrofit 常用请求默认定义，用于处理一些只能使用
 * </p>
 *
 * @author linxiao
 * @since 2018/6/11.
 */
public interface CommonApi {
    
    /**
     * 直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
     *
     * @param url 请求完整url
     * @return RxJava Observable with Response&#60ResponseBody&#62
     */
    @GET
    Observable<Response<ResponseBody>> get(@Url String url);
    
    /**
     * 直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
     *
     * @param url 请求完整url
     * @return RxJava Observable with Response&#60ResponseBody&#62
     */
    @GET
    Observable<Response<ResponseBody>> get(@Url String url, @QueryMap Map<String, Object> params);
    
    /**
     * 无参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
     *
     * @param url 请求完整url
     * @return RxJava Observable with Response&#60ResponseBody&#62
     */
    @POST
    Observable<Response<ResponseBody>> post(@Url String url);
    
    /**
     * 带参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
     *
     * @param url 请求完整url
     * @param params 请求数据，将会以form形式包装
     * @return RxJava Observable with Response&#60ResponseBody&#62
     */
    @FormUrlEncoded
    @POST
    Observable<Response<ResponseBody>> formPost(@Url String url, @FieldMap Map<String, Object> params);
    
    /**
     * 带参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
     *
     * @param url 请求完整url
     * @param params 请求数据，将会以JSON形式包装
     * @return RxJava Observable with Response&#60ResponseBody&#62
     */
    @POST
    Observable<Response<ResponseBody>> jsonPost(@Url String url, @Body Map<String, Object> params);

}
