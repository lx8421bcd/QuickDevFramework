package com.linxiao.framework.net

import android.util.Log
import okhttp3.Headers

/**
 * 承载Http信息实体
 * Created by linxiao on 2017/1/19.
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
    var requestHeaders: Headers? = null
    var requestContentType: String? = null
    var requestContentLength: Long = 0
    var requestBody: String? = null

    /*---------------response params----------------*/
    var responseHeaders: Headers? = null
    var responseCode = 0
    var responseMessage: String? = null
    var responseContentLength: Long = 0
    var responseBody: String? = null
    fun logOut() {
        logLine("----------------------------")
        logLine("url: $url")
        logLine("protocol: %s,  method: %s", protocol!!, method!!)
        logLine("request took time: %d ms", tookMills)
        logLine("response code: %d,  message: %s", responseCode, responseMessage!!)
        logLine("----------request-----------")
        logLine("Headers:")
        for (headerName in requestHeaders!!.names()) {
            logLine("%s : %s", headerName, requestHeaders!![headerName]!!)
        }
        logLine("Body:")
        logLine(requestBody)
        logLine("----------response----------")
        logLine("Headers:")
        for (headerName in responseHeaders!!.names()) {
            logLine("%s : %s", headerName, responseHeaders!![headerName]!!)
        }
        logLine("Body:")
        logLine(responseBody)
        logLine("----------------------------")
    }

    private fun logLine(message: String?) {
        if (message == null) {
            Log.i("|", "null")
            return
        }
        if (message.length < 4000) {
            Log.i("|", message)
            return
        }
        var subStart = 0
        var subEnd = 4000
        while (subEnd < message.length) {
            Log.i("|", message.substring(subStart, subEnd))
            subStart = subEnd
            subEnd += 4000
        }
        Log.i("|", message.substring(subStart))
    }

    private fun logLine(format: String, vararg args: Any) {
        logLine(String.format(format, *args))
    }

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
