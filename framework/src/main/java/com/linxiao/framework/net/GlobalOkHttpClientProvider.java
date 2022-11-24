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

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

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

    private final ClearableCookieJar cookieJar;
    private final HttpInfoCatchInterceptor infoCatchInterceptor;
    private final List<GlobalBuilderInterceptor> globalBuilderInterceptorList = new ArrayList<>();
    private OkHttpClient globalOKHttpClient;
    private final Scheduler logoutScheduler = Schedulers.newThread();
    private HttpInfoCatchListener infoCatchListener = entity -> {
        if (entity != null) {
            Observable.just(entity)
            .subscribeOn(logoutScheduler)
            .doOnNext(HttpInfoEntity::logOut)
            .subscribe(new RxSubscriber<>());
        }
    };

    private GlobalOkHttpClientProvider() {
        // config cookie persistent storage
        cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(ContextProvider.get())
        );
        // config http request and response catch
        infoCatchInterceptor = new HttpInfoCatchInterceptor();
        infoCatchInterceptor.setHttpInfoCatchListener(infoCatchListener);
        infoCatchInterceptor.setCatchEnabled(true);
    }

    public synchronized void addGlobalBuilderInterceptor(GlobalBuilderInterceptor interceptor) {
        if (interceptor != null && !globalBuilderInterceptorList.contains(interceptor)) {
            globalBuilderInterceptorList.add(interceptor);
            buildDefaultClient();
        }
    }

    public synchronized void removeGlobalBuilderInterceptor(GlobalBuilderInterceptor interceptor) {
        if (globalBuilderInterceptorList.size() > 0 && globalBuilderInterceptorList.contains(interceptor)) {
            globalBuilderInterceptorList.remove(interceptor);
            buildDefaultClient();
        }
    }

    public OkHttpClient.Builder getGlobalBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // append global settings
        builder.cookieJar(cookieJar);
        // DNS配置，灰度测试用
//        builder.dns(OkHttpDns.getInstance());

        // Https trust config, you can use trust all
        // in debug mode to catch http info more easier
//        configTrustAll(builder);

        // timeout
//        builder.connectTimeout(5, TimeUnit.SECONDS);

        // add global header example
//        builder.addNetworkInterceptor(chain -> {
//            Request.Builder headerBuilder = chain.request().newBuilder();
//            headerBuilder.addHeader("User-Agent", "USER_AGENT");
//            Request request = headerBuilder.build();
//
//            return chain.proceed(request);
//        });
        // more extra builder configs
        for (GlobalBuilderInterceptor interceptor : globalBuilderInterceptorList) {
            interceptor.onBuild(builder);
        }
        return builder;
    }


    /**
     * get default OkHttpClient instance
     * @return instance of OkHttpClient, global static
     */
    public synchronized OkHttpClient getClient() {
        if (globalOKHttpClient == null) {
            buildDefaultClient();
        }
        return globalOKHttpClient;
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

    /**
     * add network info catch interceptor for an OkHttpClient
     * <p>
     * this interceptor needs to added as the last of all interceptors during building an OkHttpClient,
     * if not, the log will not print completely when request chain was changed by the interceptors register behind
     * </p>
     *
     */
    public void appendHttpInfoCatchInterceptor(OkHttpClient.Builder builder) {
        //注意这里必须使用addNetworkInterceptor，否则无法打印完整信息
        // InfoCatchInterceptor 必须最后添加，否则无法打印之后添加的interceptor对request chain修改而产生的变更
        builder.addNetworkInterceptor(infoCatchInterceptor);
    }

    public HttpInfoCatchInterceptor getInfoCatchInterceptor() {
        return infoCatchInterceptor;
    }

    public void setInfoCatchListener(HttpInfoCatchListener infoCatchListener) {
        this.infoCatchListener = infoCatchListener;
    }

    private synchronized void buildDefaultClient() {
        OkHttpClient.Builder builder = getGlobalBuilder();
        appendHttpInfoCatchInterceptor(builder);
        globalOKHttpClient = builder.build();
    }

}
