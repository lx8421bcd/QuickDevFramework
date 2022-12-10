package com.linxiao.framework.net;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.RawRes;

import com.linxiao.framework.common.ContextProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * tool class for user to build SSL configs for TCP connect
 * <p>
 * details for class usage and attention
 * </p>
 *
 * @author linxiao
 * @since 2019-04-28
 */
public final class SSLHelper {


    /**
     * get public key from certificate file in raw folder
     * @param certificateRes resId of certificate
     * @return instance of {@link PublicKey}
     */
    public static PublicKey getPublicKey(Context context, @RawRes int certificateRes) {
        try {
            InputStream fin = context.getResources().openRawResource(certificateRes);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
            return certificate.getPublicKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get public key from local certificate file
     * @param certificatePath file path of certificate
     * @return instance of {@link PublicKey}
     */
    public static PublicKey getPublicKey(String certificatePath) {
        try {
            FileInputStream fin = new FileInputStream(certificatePath);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
            return certificate.getPublicKey();
        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * generate a instance of {@link X509TrustManager} config as trust all
     * @return instance of X509TrustManager
     */
    @SuppressLint("CustomX509TrustManager")
    public static TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // do nothing
            }
            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // do nothing
            }
        };
    }

    /**
     * generate a https HostNameVerifier with inputted urls
     * @param hostUrls accept host urls
     */
    public static HostnameVerifier getHostnameVerifier(final String[] hostUrls) {
        return (hostname, session) -> {
            for (String host : hostUrls) {
                if (host.equalsIgnoreCase(hostname)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * generate a https HostNameVerifier accept all host
     */
    public static HostnameVerifier getTrustAllVerifier() {
        return (hostname, session) -> true;
    }


    /**
     * generate a {@link KeyStore} Object from KeyStore file
     * <p>
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     * </p>
     *
     * @param keyStoreFileStream key store file input stream from local file, such as raw resource and sdcard
     * @param password key store password
     * @return {@link KeyStore} instance
     */
    public static KeyStore createKeyStore(InputStream keyStoreFileStream, String password) throws
            KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if (keyStoreFileStream == null) {
            return null;
        }
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(keyStoreFileStream, password.toCharArray());
        return keyStore;
    }

    /**
     * generate a {@link KeyStore} Object from KeyStore file
     * <p>
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     * </p>
     *
     * @param rawResId resId of certificate file in raw resource folder
     * @param password key store password
     * @return {@link KeyStore} instance
     */
    public static KeyStore createKeyStore(@RawRes int rawResId, String password)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        InputStream is = ContextProvider.get().getResources().openRawResource(rawResId);
        return createKeyStore(is, password);
    }

    /**
     * generate a {@link KeyStore} Object from KeyStore file
     * <p>
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     * </p>
     *
     * @param certFile certificate file object
     * @param password key store password
     * @return {@link KeyStore} instance
     */
    public static KeyStore createKeyStore(File certFile, String password)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        InputStream is = new FileInputStream(certFile);
        return createKeyStore(is, password);
    }

}
