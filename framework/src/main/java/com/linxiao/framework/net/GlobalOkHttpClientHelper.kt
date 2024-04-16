package com.linxiao.framework.net

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.linxiao.framework.common.globalContext
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.security.SecureRandom
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * global okhttpClient management class
 *
 * @author linxiao
 * @since 2016-11-27
 */
object GlobalOkHttpClientHelper {

    val globalHeaderMap = HashMap<String, String>()

    private val logoutExecutor = Executors.newSingleThreadExecutor()
    var globalInfoCatchListener = { entity: HttpInfoEntity ->
        Observable.fromCallable { entity.logOut() }
        .subscribeOn(Schedulers.from(logoutExecutor))
        .subscribe()
    }

    private val httpInfoCatchInterceptor: HttpInfoCatchInterceptor by lazy {
        val interceptor = HttpInfoCatchInterceptor()
        interceptor.setCatchEnabled(true)
        interceptor.setHttpInfoCatchListener {
            globalInfoCatchListener.invoke(it)
        }
        return@lazy interceptor
    }

    private val headerInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val headerBuilder = Headers.Builder()
            globalHeaderMap.forEach {
                headerBuilder.add(it.key, it.value)
            }
            val request = chain.request().newBuilder()
                .headers(headerBuilder.build())
                .build()
            chain.proceed(request)
        }
    }

    private val globalCookieJar by lazy {
        return@lazy PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(globalContext)
        )
    }
    /**
     * 默认OkHttpClient
     */
    val defaultClient by lazy {
        return@lazy getBuilder().buildWithHttpInfoCatch()
    }

    /**
     * 带全局配置的builder
     */
    fun getBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        // config cookie persistent storage
        // append global settings
        builder.cookieJar(globalCookieJar)
        // timeout
        builder.connectTimeout(5, TimeUnit.SECONDS)
        // config interceptors
        builder.addNetworkInterceptor(headerInterceptor)
        return builder
    }

    fun OkHttpClient.Builder.addGlobalHeaderInterceptor(): OkHttpClient.Builder {
        this.addNetworkInterceptor(headerInterceptor)
        return this
    }

    fun OkHttpClient.Builder.buildWithHttpInfoCatch(): OkHttpClient {
        // 注意这里必须使用addNetworkInterceptor，否则无法打印完整信息
        // InfoCatchInterceptor 必须最后添加，否则无法打印之后添加的interceptor对request chain修改而产生的变更
        this.addNetworkInterceptor(httpInfoCatchInterceptor)
        return this.build()
    }

    /**
     * 构建OkHttpClient中配置不验证证书
     */
    fun OkHttpClient.Builder.configTrustAll(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf(SSLHelper.createTrustAllTrustManager())
        try {
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            this.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            this.hostnameVerifier { hostname: String?, session: SSLSession? -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 构建证书配置
     */
    fun OkHttpClient.Builder.configTrustX509(
        certificateFilePath: String,
        certificatePassword: String
    ): OkHttpClient.Builder {
        try {
            //this file do not exist, replace with your certificate file when you use this method
            val keyStore = SSLHelper.createKeyStore(File(certificateFilePath), certificatePassword)
            val tmf = TrustManagerFactory.getInstance("X509")
            tmf.init(keyStore)
            val trustManagers = tmf.trustManagers
            val sslContext = SSLContext.getInstance("SSL")
            val sslSocketFactory = sslContext.socketFactory
            this.sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
            this.hostnameVerifier { hostname: String?, session: SSLSession? -> true }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun CookieJar.addCookie(forUrl: String, cookie: Cookie) {
        val httpUrl = forUrl.toHttpUrlOrNull()?:return
        this.saveFromResponse(httpUrl, listOf(cookie))
    }

    fun CookieJar.getCachedCookies(forUrl: String): List<Cookie> {
        val httpUrl = forUrl.toHttpUrlOrNull()?:return listOf()
        return this.loadForRequest(httpUrl)
    }
}