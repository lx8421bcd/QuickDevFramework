package com.linxiao.framework.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.linxiao.framework.BaseApplication;
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

import okhttp3.internal.platform.Platform;

/**
 * 框架下Retrofit管理类
 * Created by LinXiao on 2016-11-27.
 */
public class RetrofitManager {

    private static final String TAG = RetrofitManager.class.getSimpleName();

    /**
     * 提供Http请求的ApiBuilder
     * */
    public static RetrofitApiBuilder createRetrofitBuilder(String serverUrl) {
        return new RetrofitApiBuilder()
                .setServerUrl(serverUrl);
    }

    /**
     * 提供Https请求的ApiBuilder
     * <p>此方法为本地不存放证书时使用</p>
     * */
    public static RetrofitApiBuilder createRetrofitBuilder(String publicKey, String serverUrl) {
        X509TrustManager trustManager = getDefaultTrustManager(publicKey);
        final TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
        // Install the all-trusting trust manager
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Logger.e(TAG, e);
            return null;
        }
        // Create an ssl socket factory with our all-trusting manager
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return new RetrofitApiBuilder()
                .setServerUrl(serverUrl)
                .setSSLSocketFactory(sslSocketFactory, trustManager);
    }

    /**
     * 提供Https请求的ApiBuilder
     * <p>此方法为本地存放证书时使用</p>
     * */
    public static RetrofitApiBuilder createRetrofitBuilder(int[] certificates, String serverUrl) {
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(BaseApplication.getAppContext(), certificates);
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
        if (sslSocketFactory == null) {
            Logger.e(TAG, "sslSocketFactory is null");
            return null;
        }
        if (trustManager == null) {
            Logger.e(TAG, "trustManager is null");
            return null;
        }
        return new RetrofitApiBuilder()
                .setServerUrl(serverUrl)
                .setSSLSocketFactory(sslSocketFactory, trustManager);
    }


    /**
     * 获取本地不存放证书时的TrustTrustManager
     *
     * */
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
