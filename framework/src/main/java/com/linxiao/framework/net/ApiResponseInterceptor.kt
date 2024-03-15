package com.linxiao.framework.net

import android.util.Log
import com.linxiao.framework.json.GsonParser.fromJSONObject
import com.linxiao.framework.net.ApiResponse.Companion.isApiResponseString
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * ApiResponse拦截器, 用于进行全局错误拦截处理
 *
 * @author lx8421bcd
 * @since 2022-12-07
 */
class ApiResponseInterceptor : Interceptor {

    private val TAG = ApiResponseInterceptor::class.java.getSimpleName()
    private val apiResponseCallbackList: MutableList<(response: ApiResponse?) -> Unit> = ArrayList()

    fun addOnApiResponseInterceptCallback(callback: (response: ApiResponse?) -> Unit) {
        if (!apiResponseCallbackList.contains(callback)) {
            apiResponseCallbackList.add(callback)
        }
    }

    fun removeOnApiResponseInterceptCallback(callback: (response: ApiResponse?) -> Unit) {
        apiResponseCallbackList.remove(callback)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val responseBody = response.body
        if (responseBody == null) {
            Log.e(TAG, "intercept: null response body")
            return response
        }
        try {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire requestBody.
            var buffer = source.buffer()
            // handle gzip
            if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            if (!isPlaintext(buffer) || responseBody.contentLength() == 0L) {
                return response
            }
            val contentType = responseBody.contentType()
            val charset = if (contentType == null) StandardCharsets.UTF_8 else contentType.charset(
                StandardCharsets.UTF_8
            )
            val responseString = if (charset == null) "" else buffer.clone().readString(charset)
            if (isApiResponseString(responseString)) {
                val apiResponse = fromJSONObject(responseString, ApiResponse::class.java)
                if (apiResponse != null) {
                    for (callback in apiResponseCallbackList) {
                        callback.invoke(apiResponse)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            false // Truncated UTF-8 sequence.
        }
    }
}
