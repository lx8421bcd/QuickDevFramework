package com.linxiao.framework.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.linxiao.framework.common.ContextProvider;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Retrofit management class
 * <p>
 * provides the default retrofit config method, default OkHttpClient Builder
 * and default {@link HttpInfoCatchInterceptor} implementation.
 * see code for more details.
 * </p>
 *
 * Created by linxiao on 2016-11-27.
 */
public class RetrofitManager {

    private static final String TAG = RetrofitManager.class.getSimpleName();

    private static ClearableCookieJar cookieJar;
    private static HttpInfoCatchInterceptor infoCatchInterceptor;
    private static OkHttpClient mOkHttpClient;
    private static CommonApi commonApi;
    
    // use handler to ensure that an entity logout will print one by one
    private static Handler logHandler = new Handler(Looper.myLooper()) {
    
        @Override
        public void handleMessage(Message msg) {
            HttpInfoEntity entity = (HttpInfoEntity) msg.obj;
            if (entity == null) {
                return;
            }
            entity.logOut();
        }
    };
    
    private static HttpInfoCatchListener infoCatchListener = entity -> {
        if (entity == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = entity;
        logHandler.sendMessage(msg);
    };
    
    static {
        mOkHttpClient = getDefaultOKHttpClientBuilder().build();
        commonApi = initClientApi("https://useless.url.placeholder", CommonApi.class);
    }

    private RetrofitManager(){}
    
    /**
     * get retrofit common http request method
     * @return {@link CommonApi}
     */
    public static CommonApi getCommonApi() {
        return commonApi;
    }
    
    /**
     * init a Retrofit API declares interface class wtih default framework config
     * <p>
     * using default framework config means:
     * 1. use default OkHttpClient; <br/>
     * 2. use RxJava2CallAdapter; <br/>
     * 3. use Gson to serialize and deserialize; <br/>
     * 4. use {@link ApiConverterFactory} to convert response data; <br/>
     * </p>
     * @param baseUrl base server address for the API declares
     * @param apiClazz class object of API declares interface
     * @param <T> type define of API declares interface
     * @return instance of API interface
     */
    public static <T> T initClientApi(String baseUrl, Class<T> apiClazz) {
        Retrofit.Builder builder = new Retrofit.Builder();
        if (!TextUtils.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
            baseUrl += "/";
            builder.baseUrl(baseUrl);
        }
        builder.client(mOkHttpClient);
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.addConverterFactory(ApiConverterFactory.create());
        return builder.build().create(apiClazz);
    }

    /**
     * get CookieJar from OkHttpClient instance in the framework
     * @return cookieJar
     */
    public static CookieJar getCookieJar() {
        return mOkHttpClient.cookieJar();
    }

    /**
     * a simple method to add custom cookie into OkHttpClient int the framework
     * @param forUrl the url your cookie using for
     * @param cookie cookie body
     */
    public static void addCookie(String forUrl, Cookie cookie) {
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
    public static List<Cookie> getCachedCookies(String forUrl) {
        HttpUrl url = HttpUrl.parse(forUrl);
        if (url == null) {
            return new ArrayList<>();
        }
        return getCookieJar().loadForRequest(url);
    }
    
    /**
     * clear cookies in default OkHttpClient instance
     */
    public static void clearCookie() {
        cookieJar.clearSession();
    }
    
    /**
     * get default OkHttpClient instance
     * @return instance of OkHttpClient, global static
     */
    public static OkHttpClient getOKHttpClient() {
        return mOkHttpClient;
    }
    
    /**
     * get default OkHttpClient Builder
     * <p>see method implementation for more details</p>
     */
    public static OkHttpClient.Builder getDefaultOKHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // config cookie persistent storage
        cookieJar = new PersistentCookieJar(
          new SetCookieCache(),
          new SharedPrefsCookiePersistor(ContextProvider.get())
        );
        builder.cookieJar(cookieJar);
        
        // Https trust config, you can use trust all
        // in debug mode to catch http info more easier
        configTrustAll(builder);
        
        // if you want to do some custom header modification before request and effect on global,
        // you should do it at here
        builder.addNetworkInterceptor(chain -> {
            Request.Builder builder1 = chain.request().newBuilder();
            //TODO ADD YOUR CUSTOM HEADER HERE
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
        
        return builder;
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
    public static void setHttpInfoCatchEnabled(boolean enabled) {
        infoCatchInterceptor.setCatchEnabled(enabled);
    }
    
    /**
     * https TrustAll 配置，如果需要抓包的话可以打开此配置，否则没有必要
     * */
    public static void configTrustAll(OkHttpClient.Builder builder) {
        final TrustManager[] trustAllCerts = new TrustManager[] {SSLHelper.createTrustAllTrustManager()};
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SSL single certificate config for OkHttpClient
     */
    public static void configSingleTrust(OkHttpClient.Builder builder) {
        try {
            //this file do not exist, replace with your certificate file when you use this method
            KeyStore keyStore = SSLHelper.createKeyStore(new File("test.bks"), "123456");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
