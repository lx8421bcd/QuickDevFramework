package com.linxiao.framework.net

import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

/**
 * instance of OkHttp interceptor
 *
 *
 * Used to catch http request and response info and
 * save as [HttpInfoEntity].
 *
 * Use method [okhttp3.OkHttpClient.Builder.addNetworkInterceptor] to register
 * [HttpInfoCatchInterceptor] instance into OkHttpClient, if registered instance using
 * [okhttp3.OkHttpClient.Builder.addInterceptor],
 * you will unable to get complete network messages.
 *
 *
 * @author lx8421bcd
 * @since  2016-12-04.
 */
class HttpInfoCatchInterceptor : Interceptor {

    private val UTF8 = StandardCharsets.UTF_8
    var httpInfoCatchListener: ((info: HttpInfoEntity) -> Unit)? = null
        private set
    private var catchEnabled = false

    /**
     * set catch http info enabled
     * @param enabled enabled
     */
    fun setCatchEnabled(enabled: Boolean) {
        catchEnabled = enabled
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (!catchEnabled || httpInfoCatchListener == null) {
            return chain.proceed(request)
        }
        val entity = HttpInfoEntity()
        val requestBody = request.body
        val connection: Connection? = chain.connection()
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1
        entity.protocol = protocol.toString()
        entity.method = request.method
        entity.url = request.url.toString()
        entity.requestHeaders = request.headers
        if (requestBody != null) {
            entity.requestContentType = requestBody.contentType().toString()
            entity.requestContentLength = requestBody.contentLength()
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            if (charset != null) {
                entity.requestBody = buffer.readString(charset)
            }
        }
        //-----request prepare----
        val startNs = System.nanoTime()
        val response: Response = chain.proceed(request)
        //-----request done--------
        entity.tookMills = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        entity.responseHeaders = response.headers
        entity.responseCode = response.code
        entity.responseMessage = response.message
        val responseBody = response.body
        if (responseBody != null) {
            entity.responseContentLength = responseBody.contentLength()
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire requestBody.
            var buffer = source.buffer()

            // handle gzip
            if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                var gzippedResponseBody: GzipSource? = null
                try {
                    gzippedResponseBody = GzipSource(buffer.clone())
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                } finally {
                    gzippedResponseBody?.close()
                }
            }
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    entity.responseBody = "unreadable, charset error"
                }
            }
            if (isPlaintext(buffer) && responseBody.contentLength() != 0L) {
                if (charset != null) {
                    entity.responseBody = buffer.clone().readString(charset)
                }
            } else {
                entity.responseBody = "unreadable, not text"
            }
        }
        httpInfoCatchListener!!.invoke(entity)
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

    fun setHttpInfoCatchListener(httpInfoCatchListener: (info: HttpInfoEntity) -> Unit) {
        this.httpInfoCatchListener = httpInfoCatchListener
    }
}
