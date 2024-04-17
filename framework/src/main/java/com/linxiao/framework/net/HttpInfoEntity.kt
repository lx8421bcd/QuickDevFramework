package com.linxiao.framework.net

import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Headers
import java.util.concurrent.Executors

/**
 * 承载Http信息实体
 *
 * @author lx8421bcd
 * @since 2017-01-19
 */
class HttpInfoEntity {
    /**Http协议 */
    var protocol: String? = null

    /**请求方式 */
    var method: String? = null

    /**请求地址 */
    var url: String? = null

    /**请求耗时 */
    var tookMills: Long = 0

    /*---------------request params----------------*/
    var requestHeaders: Headers = Headers.Builder().build()
    var requestContentType: String? = null
    var requestContentLength: Long = 0
    var requestBody: String? = null

    /*---------------response params----------------*/
    var responseHeaders: Headers = Headers.Builder().build()
    var responseCode = 0
    var responseMessage: String? = null
    var responseContentLength: Long = 0
    var responseBody: String? = null

    override fun toString(): String {
        return "HttpInfoEntity{" +
                "protocol='" + protocol + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", tookMills=" + tookMills +
                ", requestHeaders=" + requestHeaders +
                ", requestContentType='" + requestContentType + '\'' +
                ", requestContentLength=" + requestContentLength +
                ", requestBody='" + requestBody + '\'' +
                ", responseHeaders=" + responseHeaders +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseContentLength=" + responseContentLength +
                ", responseBody='" + responseBody + '\'' +
                '}'
    }
}

private val logoutExecutor = Executors.newSingleThreadExecutor()

fun HttpInfoEntity.logOut() {
    Observable.fromCallable {
        logLine("----------------------------")
        logLine("url: $url")
        logLine("protocol: ${protocol},  method: $method")
        logLine("request took time: $tookMills ms")
        logLine("response code: ${responseCode},  message: $responseMessage")
        logLine("----------request-----------")
        logLine("Headers:")
        for (headerName in requestHeaders.names()) {
            logLine("$headerName : ${requestHeaders[headerName]}")
        }
        logLine("Body:")
        logLine(requestBody)
        logLine("----------response----------")
        logLine("Headers:")
        for (headerName in responseHeaders.names()) {
            logLine("$headerName : ${responseHeaders[headerName]}")
        }
        logLine("Body:")
        logLine(responseBody)
        logLine("----------------------------")
    }
    .subscribeOn(Schedulers.from(logoutExecutor))
    .subscribe()
}

private fun logLine(message: String?) {
    if (message == null) {
        Log.i("|", "null")
        return
    }
    if (message.length < 3500) {
        Log.i("|", message)
        return
    }
    var subStart = 0
    var subEnd = 3500
    while (subEnd < message.length) {
        Log.i("|", message.substring(subStart, subEnd))
        subStart = subEnd
        subEnd += 3500
    }
    Log.i("|", message.substring(subStart))
}