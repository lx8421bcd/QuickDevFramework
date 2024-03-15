package com.linxiao.framework.net

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.RawRes
import com.linxiao.framework.common.globalContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * tool class for user to build SSL configs for TCP connect
 *
 *
 * details for class usage and attention
 *
 *
 * @author linxiao
 * @since 2019-04-28
 */
object SSLHelper {
    /**
     * get public key from certificate file in raw folder
     * @param certificateRes resId of certificate
     * @return instance of [PublicKey]
     */
    fun getPublicKey(context: Context, @RawRes certificateRes: Int): PublicKey? {
        try {
            val fin = context.resources.openRawResource(certificateRes)
            val f = CertificateFactory.getInstance("X.509")
            val certificate = f.generateCertificate(fin) as X509Certificate
            return certificate.publicKey
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get public key from local certificate file
     * @param certificatePath file path of certificate
     * @return instance of [PublicKey]
     */
    fun getPublicKey(certificatePath: String?): PublicKey? {
        try {
            val fin = FileInputStream(certificatePath)
            val f = CertificateFactory.getInstance("X.509")
            val certificate = f.generateCertificate(fin) as X509Certificate
            return certificate.publicKey
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * generate a instance of [X509TrustManager] config as trust all
     * @return instance of X509TrustManager
     */
    @SuppressLint("CustomX509TrustManager")
    fun createTrustAllTrustManager(): TrustManager {
        return object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                // do nothing
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                // do nothing
            }
        }
    }

    /**
     * generate a https HostNameVerifier with inputted urls
     * @param hostUrls accept host urls
     */
    fun getHostnameVerifier(hostUrls: Array<String>): HostnameVerifier {
        return HostnameVerifier { hostname: String?, session: SSLSession? ->
            for (host in hostUrls) {
                if (host.equals(hostname, ignoreCase = true)) {
                    return@HostnameVerifier true
                }
            }
            false
        }
    }

    val trustAllVerifier: HostnameVerifier
        /**
         * generate a https HostNameVerifier accept all host
         */
        get() = HostnameVerifier { hostname: String?, session: SSLSession? -> true }

    /**
     * generate a [KeyStore] Object from KeyStore file
     *
     *
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     *
     *
     * @param keyStoreFileStream key store file input stream from local file, such as raw resource and sdcard
     * @param password key store password
     * @return [KeyStore] instance
     */
    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )
    fun createKeyStore(keyStoreFileStream: InputStream?, password: String): KeyStore? {
        if (keyStoreFileStream == null) {
            return null
        }
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(keyStoreFileStream, password.toCharArray())
        return keyStore
    }

    /**
     * generate a [KeyStore] Object from KeyStore file
     *
     *
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     *
     *
     * @param rawResId resId of certificate file in raw resource folder
     * @param password key store password
     * @return [KeyStore] instance
     */
    @Throws(
        CertificateException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        IOException::class
    )
    fun createKeyStore(@RawRes rawResId: Int, password: String): KeyStore? {
        val `is` = globalContext.resources.openRawResource(rawResId)
        return createKeyStore(`is`, password)
    }

    /**
     * generate a [KeyStore] Object from KeyStore file
     *
     *
     * attention: Android only support BKS format key store, which means you have to use
     * ".bks" certificate file format, you have to convert other key store format to bks
     * to use it in Android
     *
     *
     * @param certFile certificate file object
     * @param password key store password
     * @return [KeyStore] instance
     */
    @Throws(
        CertificateException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        IOException::class
    )
    fun createKeyStore(certFile: File?, password: String): KeyStore? {
        val `is`: InputStream = FileInputStream(certFile)
        return createKeyStore(`is`, password)
    }
}
