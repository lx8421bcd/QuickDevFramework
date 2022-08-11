package com.linxiao.framework.net;

import java.io.File;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class OkHttpClientHelper {

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
