package com.linxiao.framework.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.linxiao.framework.BaseApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *
 * Created by LinXiao on 2016-11-27.
 */
public class RetrofitApiManager {

    private static final String TAG = RetrofitApiManager.class.getSimpleName();

    public static RetrofitApiBuilder buildClientAPI() {
        return new RetrofitApiBuilder();
    }

    protected static SSLSocketFactory getSSLSocketFactory(int[] certificates) {
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certificates.length; i++) {
                InputStream certificate = BaseApplication.getAppContext().getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(certificate));

                if (certificate != null) {
                    certificate.close();
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static HostnameVerifier getHostnameVerifier(final String[] hostUrls) {

        HostnameVerifier TRUSTED_VERIFIER = new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                boolean ret = false;
                for (String host : hostUrls) {
                    if (host.equalsIgnoreCase(hostname)) {
                        ret = true;
                    }
                }
                return ret;
            }
        };

        return TRUSTED_VERIFIER;
    }

    @NonNull
    public static Interceptor provideHttpReqInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "application/json; charset=UTF-8")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Accept", "*/*");
//                        .addHeader("User-Agent", GlobalConfig.USER_AGENT);
                if (SessionManager.getSession() != null) {
                    Log.d("Request-Cookie", SessionManager.getSession());
                    builder.addHeader("Cookie", SessionManager.getSession());
                }
                Request request = builder.build();
                Log.d(TAG, "request url = " + request.url().toString());
                return chain.proceed(request);
            }
        };
    }

    public static class RetrofitApiBuilder {

        private Retrofit.Builder mRetrofitBuilder;
        private OkHttpClient.Builder okHttpClientBuilder;

        public RetrofitApiBuilder() {
            mRetrofitBuilder = new Retrofit.Builder();
            okHttpClientBuilder = new OkHttpClient.Builder();
        }

        public RetrofitApiBuilder setServerUrl(String serverUrl) {
            mRetrofitBuilder.baseUrl(serverUrl);
            return this;
        }

        public RetrofitApiBuilder addCookieManager() {
            okHttpClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    //存入Session
                    if (response.header("Set-Cookie") != null) {
                        SessionManager.setSession(response.header("Set-Cookie"));
                    }
                    //刷新API调用时间
                    SessionManager.setLastApiCallTime(System.currentTimeMillis());

                    return response;
                }
            });
            return this;
        }

        public RetrofitApiBuilder addCustomInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addInterceptor(interceptor);
            return this;
        }

        public RetrofitApiBuilder addHttpsSupport(int[] certificates, String[] hostUrls) {
            okHttpClientBuilder.socketFactory(getSSLSocketFactory(certificates));
            okHttpClientBuilder.hostnameVerifier(getHostnameVerifier(hostUrls));
            return this;
        }

        public RetrofitApiBuilder setCustomOkHttpClient(OkHttpClient okHttpClient) {
            mRetrofitBuilder.client(okHttpClient);
            return this;
        }

        public RetrofitApiBuilder addCallAdapterFactory(CallAdapter.Factory factory) {
            mRetrofitBuilder.addCallAdapterFactory(factory);
            return this;
        }

        public RetrofitApiBuilder addConvertFactory(Converter.Factory factory) {
            mRetrofitBuilder.addConverterFactory(factory);
            return this;
        }


        public <T> T  build(Class<T> clazzClientApi) {
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            for (Interceptor interceptor : interceptors) {
//                builder.addInterceptor(interceptor);
//            }
//            OkHttpClient okHttpClient = builder.build();
//            mRetrofitBuilder.client(okHttpClient);

            return mRetrofitBuilder.build().create(clazzClientApi);
        }
    }

}
