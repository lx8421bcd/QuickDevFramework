package com.linxiao.framework.net;

import com.linxiao.framework.BaseApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *
 * Created by LinXiao on 2016-11-27.
 */
public class RetrofitApiManager {

    private static final String TAG = RetrofitApiManager.class.getSimpleName();

    public static RetrofitAPIBuilder buildClientAPI() {
        return new RetrofitAPIBuilder();
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

    public static class RetrofitAPIBuilder {

        private Retrofit.Builder mRetrofitBuilder;
        private ArrayList<Interceptor> interceptors;

        public RetrofitAPIBuilder() {
            mRetrofitBuilder = new Retrofit.Builder();
            interceptors = new ArrayList<>();
        }

        public RetrofitAPIBuilder setServerUrl(String serverUrl) {
            mRetrofitBuilder.baseUrl(serverUrl);
            return this;
        }

        public RetrofitAPIBuilder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public RetrofitAPIBuilder setOkHttpClient(OkHttpClient okHttpClient) {
            mRetrofitBuilder.client(okHttpClient);
            return this;
        }

        public RetrofitAPIBuilder addCallAdapterFactory(CallAdapter.Factory factory) {
            mRetrofitBuilder.addCallAdapterFactory(factory);
            return this;
        }

        public RetrofitAPIBuilder addConvertFactory(Converter.Factory factory) {
            mRetrofitBuilder.addConverterFactory(factory);
            return this;
        }

        public RetrofitAPIBuilder addHttpsSupport() {
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
