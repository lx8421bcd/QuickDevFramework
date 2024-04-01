package com.linxiao.framework.net

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import com.linxiao.framework.common.BitmapUtil
import com.linxiao.framework.common.globalContext
import com.linxiao.framework.permission.PermissionException
import com.linxiao.framework.permission.PermissionUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

/**
 * simple image upload tool
 *
 *
 *
 * used to handle some simple image upload demand such like upload avatar.
 * default upload limit size is 10MB, you can change this attribute by using
 * [.setUploadLimitSize].
 * default User-Agent is application package name, use [.setUserAgent]
 * to change it globally
 *
 * general procedure of image upload is set image local path and remote upload url,
 * execute upload program and get upload result, which contains uploaded image url.
 * so the onNext method of Subscriber will returns a JSONObject that convert by
 * http response body, considering that the response format is determined by the backend
 * and is actually unknown, this tool do not provide more result parsing
 *
 *
 *
 * @author lx8421bcd
 * @since 2019-12-04
 */
class SimpleImageUploader private constructor(val filePath: String, val uploadUrl: String) {

    companion object {
        private val TAG = SimpleImageUploader::class.java.getSimpleName()
        private const val DEFAULT_UPLOAD_LIMIT_SIZE = 10485760 // default limit 10MB
        private val DEFAULT_USER_AGENT = globalContext.packageName
        private var mUserAgent = DEFAULT_USER_AGENT

        /**
         * set User-Agent string
         */
        fun setUserAgent(userAgent: String) {
            mUserAgent = userAgent
        }

        fun newInstance(filePath: String, uploadUrl: String): SimpleImageUploader {
            return SimpleImageUploader(filePath, uploadUrl)
        }
    }
    
    private var method = "POST"
    var cookieString: String? = null
        private set
    private val requestParams: MutableMap<String, Any> = HashMap()
    private var limitSize = DEFAULT_UPLOAD_LIMIT_SIZE

    /**
     * set upload limit size, image will be compressed that over this limit
     * @param limitSize limit size
     */
    fun setUploadLimitSize(limitSize: Int): SimpleImageUploader {
        this.limitSize = limitSize
        return this
    }

    /**
     * set upload request method, usually POST or GET
     * using POST by default
     * @param method method name
     */
    fun setMethod(method: String): SimpleImageUploader {
        this.method = method
        return this
    }

    /**
     * add additional request params
     * @param key key
     * @param value value
     */
    fun putRequestParams(key: String, value: Any): SimpleImageUploader {
        requestParams[key] = value
        return this
    }

    fun setCookieString(cookieString: String?): SimpleImageUploader {
        this.cookieString = cookieString
        return this
    }

    fun start(): Observable<JSONObject> {
        return if (!PermissionUtil.hasSDCardPermission()) {
            Observable.error(PermissionException())
        } else Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<JSONObject> ->
            val boundary = UUID.randomUUID().toString()
            val prefix = "--"
            val lineEnd = "\r\n"
            val contentType = "multipart/form-data"
            val uploadFile = File(filePath)
            if (!uploadFile.exists()) {
                emitter.onError(FileNotFoundException())
                return@ObservableOnSubscribe
            }
            if (!uploadFile.isFile()) {
                emitter.onError(FileNotFoundException("target is not a file"))
                return@ObservableOnSubscribe
            }
            val fileName = uploadFile.getName()
            Log.d(TAG, "filePath = $filePath")
            Log.d(TAG, "fileName = $fileName")
            var uploadImg = BitmapUtil.getBitmap(filePath)
            val quality = BitmapUtil.getCompressQuality(uploadImg, uploadFile.length())
            Log.d(TAG, "quality: $quality")
            Log.d(TAG, "limitSize: $limitSize")
            uploadImg = BitmapUtil.matrixCompressByLimit(uploadImg, quality, limitSize)
            try {
                Log.d(TAG, "upload url: $uploadUrl")
                val url = URL(uploadUrl)
                trustEveryone()
                val connection = url.openConnection() as HttpsURLConnection
                connection.setReadTimeout(10000)
                connection.setConnectTimeout(10000)
                connection.setDoInput(true)
                connection.setDoOutput(true)
                connection.setUseCaches(false)
                connection.setRequestMethod(method)
                connection.setRequestProperty("User-Agent", mUserAgent)
                connection.setRequestProperty("Charset", "UTF-8")
                connection.setRequestProperty("Connection", "keep-alive")
                connection.setRequestProperty("Content-Type", "$contentType;boundary=$boundary")
                if (!TextUtils.isEmpty(cookieString)) {
                    connection.setRequestProperty("Cookie", cookieString)
                }
                val dos = DataOutputStream(connection.outputStream)
                for (key in requestParams.keys) {
                    dos.writeBytes(prefix + boundary)
                    dos.writeBytes(lineEnd)
                    dos.writeBytes("Content-Disposition: form-data; name=\"$key\"$lineEnd")
                    dos.writeBytes("Content-Type: text/plain$lineEnd")
                    dos.writeBytes(lineEnd)
                    dos.writeBytes(requestParams[key].toString())
                    dos.writeBytes(lineEnd)
                }
                dos.writeBytes(prefix + boundary)
                dos.writeBytes(lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$lineEnd")
                dos.writeBytes("Content-Type: image/jpeg; charset=UTF-8$lineEnd")
                dos.writeBytes(lineEnd)
                val baos = ByteArrayOutputStream()
                uploadImg.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                val inputStream: InputStream = ByteArrayInputStream(baos.toByteArray())
                val bytes = ByteArray(1024)
                var len: Int
                while (inputStream.read(bytes).also { len = it } != -1) {
                    dos.write(bytes, 0, len)
                }
                inputStream.close()
                dos.writeBytes(lineEnd)
                dos.writeBytes(prefix + boundary + prefix + lineEnd)
                dos.flush()
                dos.close()
                val code = connection.getResponseCode()
                Log.d(TAG, "code: $code")
                if (code != 200) {
                    emitter.onError(IOException("upload failed($code)"))
                    return@ObservableOnSubscribe
                }
                val input = connection.inputStream
                val sb1 = StringBuilder()
                var ss: Int
                while (input.read().also { ss = it } != -1) {
                    sb1.append(ss.toChar())
                }
                val result = sb1.toString()
                Log.d(TAG, "result: $result")
                emitter.onNext(JSONObject(result))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        } as ObservableOnSubscribe<JSONObject>)
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CustomX509TrustManager")
    private fun trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { hostname: String?, session: SSLSession? -> true }
            val context = SSLContext.getInstance("TLS")
            context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
        } catch (e: Exception) { // should never happen
            e.printStackTrace()
        }
    }
}
