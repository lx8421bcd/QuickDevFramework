package com.linxiao.quickdevframework.sample.netapi;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 *
 * Created by LinXiao on 2016-12-04.
 */
public interface ClientApi {

    @GET("adat/sk/{cityId}.html")
    Observable<ResponseBody> getWeather(@Path("cityId") String cityId);
}
