package com.linxiao.framework.net;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.rx.RxSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * global okhttpClient management class
 *
 * Created by linxiao on 2016-11-27.
 */
public class GlobalOkHttpClientProvider {

    public interface GlobalBuilderInterceptor {

        void onBuild(OkHttpClient.Builder builder);

    }

    private static GlobalOkHttpClientProvider instance;

    public static GlobalOkHttpClientProvider getInstance() {
        if (instance == null) {
            instance = new GlobalOkHttpClientProvider();
        }
        return instance;
    }

    private ClearableCookieJar cookieJar;
    private HttpInfoCatchInterceptor infoCatchInterceptor;
    private final List<GlobalBuilderInterceptor> globalBuilderInterceptorList = new ArrayList<>();
    private OkHttpClient globalOKHttpClient;
    private final Scheduler logoutScheduler = Schedulers.newThread();
    private final HttpInfoCatchListener infoCatchListener = entity -> {
        if (entity == null) {
            return;
        }
        Observable.just(entity)
        .subscribeOn(logoutScheduler)
        .doOnNext(HttpInfoEntity::logOut)
        .subscribe(new RxSubscriber<>());
    };

    private GlobalOkHttpClientProvider() {
        init();
    }

    private void init() {

        globalOKHttpClient = getDefaultOKHttpClientBuilder().build();
    }

    public void addGlobalBuilderInterceptor(GlobalBuilderInterceptor interceptor) {
        if (interceptor != null && !globalBuilderInterceptorList.contains(interceptor)) {
            globalBuilderInterceptorList.add(interceptor);
        }
    }

    public void removeGlobalBuilderInterceptor(GlobalBuilderInterceptor interceptor) {
        globalBuilderInterceptorList.remove(interceptor);
    }

    /**
     * get default OkHttpClient Builder
     * <p>see method implementation for more details</p>
     */
    public OkHttpClient.Builder getDefaultOKHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // config cookie persistent storage
        cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(ContextProvider.get())
        );
        builder.cookieJar(cookieJar);
        // DNS配置，灰度测试用
//        builder.dns(OkHttpDns.getInstance());

        // Https trust config, you can use trust all
        // in debug mode to catch http info more easier
//        configTrustAll(builder);

        // timeout
        builder.connectTimeout(5, TimeUnit.SECONDS);

        // if you want to do some custom header modification before request and effect on global,
        // you should do it at here
        builder.addNetworkInterceptor(chain -> {
            Request.Builder builder1 = chain.request().newBuilder();
//            builder.addHeader("User-Agent", USER_AGENT);
            Request request = builder1.build();

            return chain.proceed(request);
        });
        // config http request and response catch
        infoCatchInterceptor = new HttpInfoCatchInterceptor();
        infoCatchInterceptor.setHttpInfoCatchListener(infoCatchListener);
        infoCatchInterceptor.setCatchEnabled(true);
        //注意这里必须使用addNetworkInterceptor，否则无法打印完整信息
        builder.addNetworkInterceptor(infoCatchInterceptor);
        // more extra builder configs
        for (GlobalBuilderInterceptor interceptor : globalBuilderInterceptorList) {
            interceptor.onBuild(builder);
        }
        return builder;
    }

    /**
     * get CookieJar from OkHttpClient instance in the framework
     * @return cookieJar
     */
    public CookieJar getCookieJar() {
        return globalOKHttpClient.cookieJar();
    }

    /**
     * a simple method to add custom cookie into OkHttpClient int the framework
     * @param forUrl the url your cookie using for
     * @param cookie cookie body
     */
    public void addCookie(String forUrl, Cookie cookie) {
        HttpUrl url = HttpUrl.parse(forUrl);
        if (url == null) {
            return;
        }
        getCookieJar().saveFromResponse(url, Collections.singletonList(cookie));
    }

    /**
     * load cached cookies from framework network module
     * @return list of cookie
     */
    public List<Cookie> getCachedCookies(String forUrl) {
        HttpUrl url = HttpUrl.parse(forUrl);
        if (url == null) {
            return new ArrayList<>();
        }
        return getCookieJar().loadForRequest(url);
    }

    /**
     * clear cookies in default OkHttpClient instance
     */
    public void clearCookie() {
        cookieJar.clearSession();
    }

    /**
     * get default OkHttpClient instance
     * @return instance of OkHttpClient, global static
     */
    public OkHttpClient getClient() {
        return globalOKHttpClient;
    }

    /**
     * set http info catch enabled
     * <p>
     * enable http info catch will completely print request and response details
     * during a single http request
     * </p>
     *
     * @param enabled 是否启用
     * */
    public void setHttpInfoCatchEnabled(boolean enabled) {
        infoCatchInterceptor.setCatchEnabled(enabled);
    }
}