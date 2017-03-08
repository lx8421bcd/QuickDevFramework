package com.linxiao.quickdevframework.sample.netapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import static com.linxiao.framework.net.FrameworkNetConstants.ADD_COOKIE_HEADER_STRING;

/**
 *
 * Created by LinXiao on 2016-12-04.
 */
public interface ClientApi {

    @Headers(ADD_COOKIE_HEADER_STRING)
    @GET("adat/sk/{cityId}.html")
    Call<ResponseBody> getWeather(@Path("cityId") String cityId);
}
