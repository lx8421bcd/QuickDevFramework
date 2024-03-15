package com.linxiao.quickdevframework.sample.netapi

import com.linxiao.framework.architecture.BaseDataManager
import com.linxiao.framework.net.GlobalOkHttpClientHelper
import com.linxiao.framework.net.GlobalOkHttpClientHelper.configTrustAll
import com.linxiao.framework.net.HttpInfoCatchInterceptor
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 分层架构简单示例
 * Created by linxiao on 2017/7/13.
 */
class NetTestDataManager : BaseDataManager() {
    private val clientApi: ClientApi

    init {
        // 这些配置可以放在App工程的网络模块中，这里简要处理就不写了
        val infoCatchInterceptor = HttpInfoCatchInterceptor()
        infoCatchInterceptor.setCatchEnabled(true)
        infoCatchInterceptor.setHttpInfoCatchListener {
            it.logOut()
        }
        val okBuilder = GlobalOkHttpClientHelper.getBuilder()
        //        okBuilder.cookieJar(RetrofitManager.());
        okBuilder.addInterceptor(infoCatchInterceptor)
        okBuilder.configTrustAll()
        val builder = Retrofit.Builder()
        builder.baseUrl("http://www.weather.com.cn/")
            .client(okBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
        clientApi = builder.build().create(ClientApi::class.java)
    }

    val testData: Observable<String>
        /**
         * 获取测试数据
         */
        get() = clientApi.getWeather("101010100")
            .flatMap { responseBody -> Observable.just(responseBody.string()) }
}
