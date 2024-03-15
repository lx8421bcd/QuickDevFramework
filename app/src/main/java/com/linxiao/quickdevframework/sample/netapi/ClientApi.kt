package com.linxiao.quickdevframework.sample.netapi

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * Created by linxiao on 2016-12-04.
 */
interface ClientApi {
    @GET("adat/sk/{cityId}.html")
    fun getWeather(@Path("cityId") cityId: String?): Observable<ResponseBody?>
}
