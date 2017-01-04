package com.linxiao.framework.net;

import com.linxiao.framework.BaseApplication;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * Created by LinXiao on 2016-11-27.
 */
public class FrameworkRetrofitManager {

    private static final String TAG = FrameworkRetrofitManager.class.getSimpleName();


    /**
     * 提供Http请求的ApiBuilder
     * */
    public static RetrofitApiBuilder createHttpRetrofitBuilder(String serverUrl) {
        return new RetrofitApiBuilder()
                .setServerUrl(serverUrl);
    }

    /**
     * 提供Https请求的ApiBuilder
     * */
    public void createHttpsRetrofitBuilder() {
        //如何比较自由的配置OKHttpClient，而不将其与Retrofit高度绑定
        //这里需要传入
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

}
