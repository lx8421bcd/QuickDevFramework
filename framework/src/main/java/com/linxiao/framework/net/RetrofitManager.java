package com.linxiao.framework.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.linxiao.framework.QDFApplication;
import com.linxiao.framework.log.Logger;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;

/**
 * 框架下Retrofit管理类
 * Created by linxiao on 2016-11-27.
 */
public class RetrofitManager {

    private static final String TAG = RetrofitManager.class.getSimpleName();
    
    /**
     * 生成用于OKHttpClient持久化Cookie存储的PersistentCookieJar
     * @return instance of {@link PersistentCookieJar}
     * */
    public static PersistentCookieJar generatePersistentCookieJar() {
        return new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(QDFApplication.getAppContext())
        );
    }
    
    /**
     * https TrustAll 配置，如果需要抓包的话可以打开此配置，否则没有必要
     * */
    public static OkHttpClient.Builder configTrustAll(OkHttpClient.Builder builder) {
        final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {}
            
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {}
            
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }
    
    /**
     * 信任配置
     * <p>本地不存放证书时使用</p>
     * */
    public static OkHttpClient.Builder configTrust(OkHttpClient.Builder builder, String publicKey) {
        X509TrustManager trustManager = getDefaultTrustManager(publicKey);
        final TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
        // Install the all-trusting trust manager
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Logger.e(TAG, e);
            return builder;
        }
        // Create an ssl socket factory with our all-trusting manager
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        return builder;
    }
    
    /**
     * 信任配置
     * <p>本地存放证书时使用</p>
     * */
    public static OkHttpClient.Builder configTrust(OkHttpClient.Builder builder, int[] certificates) {
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(QDFApplication.getAppContext(), certificates);
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
        if (sslSocketFactory == null) {
            Logger.e(TAG, "sslSocketFactory is null");
            return builder;
        }
        if (trustManager == null) {
            Logger.e(TAG, "trustManager is null");
            return builder;
        }
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        return builder;
    }
    
    private static X509TrustManager getDefaultTrustManager(final String publicKey) {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (chain == null) {
                    throw new IllegalArgumentException("checkServerTrusted:x509Certificate array is null");
                }
                if (chain.length <= 0) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                }
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
                    throw new CertificateException("checkServerTrusted: AuthType is not RSA");
                }
                // Perform customary SSL/TLS checks
                try {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                    tmf.init((KeyStore) null);
                    for (TrustManager trustManager : tmf.getTrustManagers()) {
                        ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                    }
                } catch (Exception e) {
                    throw new CertificateException(e);
                }
                // Hack ahead: BigInteger and toString(). We know a DER encoded Public Key begins
                // with 0×30 (ASN.1 SEQUENCE and CONSTRUCTED), so there is no leading 0×00 to drop.
                RSAPublicKey pubKey = (RSAPublicKey) chain[0].getPublicKey();
                String encoded = new BigInteger(1 /* positive */, pubKey.getEncoded()).toString(16);
                // Pin it!
                final boolean expected = publicKey.equalsIgnoreCase(encoded);

                if (!expected) {
                    throw new CertificateException("checkServerTrusted: Expected public key: "
                            + publicKey + ", got public key:" + encoded);
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static SSLSocketFactory getSSLSocketFactory(@NonNull Context context, int[] certificates) {
        //CertificateFactory用来证书生成
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            //Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certificates.length; i++) {
                //读取本地证书
                InputStream is = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(is));
                if (is != null) {
                    is.close();
                }
            }
            //Create a TrustManager that trusts the CAs in our keyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            //Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return null;
    }

    protected static HostnameVerifier getHostnameVerifier(final String[] hostUrls) {
        return new HostnameVerifier() {

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
    }

}
