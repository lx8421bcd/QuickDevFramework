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
 * retrofit 常用请求默认定义，用于处理一些只能使用
 * </p>
 *
 * @author linxiao
 * @since 2018/6/11.
 */
public class CommonApiProvider extends RetrofitApiProvider<CommonApiProvider.CommonApi> {

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
        Observable<Response<ResponseBody>> postForm(@Url String url, @FieldMap Map<String, Object> params);

        /**
         * 带参数直接返回OKHttp ResponseBody 的get请求，需要自己解析ResponseBody
         *
         * @param url 请求完整url
         * @param params 请求数据，将会以JSON形式包装
         * @return RxJava Observable with Response&#60ResponseBody&#62
         */
        @POST
        Observable<Response<ResponseBody>> postJson(@Url String url, @Body Map<String, Object> params);

        /**
         * 无参数get请求，返回{@link ApiResponse}对象
         * <p>必须明确接口返回值为{@link ApiResponse}声明格式再使用此接口，否则会产生解析异常</p>
         * @param url 请求完整url
         * @return RxJava Observable with ApiResponse
         */
        @GET
        Observable<ApiResponse> apiGet(@Url String url);

        /**
         * 带参数get请求，返回{@link ApiResponse}对象
         * <p>必须明确接口返回值为{@link ApiResponse}声明格式再使用此接口，否则会产生解析异常</p>
         * @param url 请求完整url
         * @param queryMap 请求参数表
         * @return RxJava Observable with ApiResponse
         */
        @GET
        Observable<ApiResponse> apiGet(@Url String url, @QueryMap Map<String, Object> queryMap);

        /**
         * 无参数post请求，返回{@link ApiResponse}对象
         * <p>必须明确接口返回值为{@link ApiResponse}声明格式再使用此接口，否则会产生解析异常</p>
         * @param url 请求完整url
         * @return RxJava Observable with ApiResponse
         */
        @POST
        Observable<ApiResponse> apiPost(@Url String url);

        /**
         * 带参数post请求，返回{@link ApiResponse}对象
         * <p>必须明确接口返回值为{@link ApiResponse}声明格式再使用此接口，否则会产生解析异常</p>
         * @param url 请求完整url
         * @param formMap 请求参数表，将会以form形式包装
         * @return RxJava Observable with ApiResponse
         */
        @FormUrlEncoded
        @POST
        Observable<ApiResponse> apiPostForm(@Url String url, @FieldMap Map<String, Object> formMap);

        /**
         * 带参数post请求，返回{@link ApiResponse}对象
         * <p>必须明确接口返回值为{@link ApiResponse}声明格式再使用此接口，否则会产生解析异常</p>
         * @param url 请求完整url
         * @param requestBody 请求参数表，将会以JSON形式包装
         * @return RxJava Observable with ApiResponse
         */
        @POST
        Observable<ApiResponse> apiPostJson(@Url String url, @Body Map<String, Object> requestBody);

    }

    private static CommonApiProvider instance;

    public static CommonApiProvider getInstance() {
        if (instance == null) {
            instance = new CommonApiProvider();
        }
        return instance;
    }

    @Override
    protected String getApiBaseUrl() {
        return "useless.host";
    }

    @Override
    protected Class<CommonApi> getApiClass() {
        return CommonApi.class;
    }
}
