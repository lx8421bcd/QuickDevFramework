package com.linxiao.framework.net;


import com.linxiao.framework.support.log.Logger;

import okhttp3.Headers;

/**
 * 承载Http信息实体
 * Created by linxiao on 2017/1/19.
 */
public class HttpInfoEntity {

    /**Http协议*/
    public String protocol;
    /**请求方式*/
    public String method;
    /**请求地址*/
    public String url;
    /**请求耗时*/
    public long tookMills = 0;

    /*---------------request params----------------*/

    public Headers requestHeaders;

    public String requestContentType;

    public long requestContentLength;

    public String requestBody;

    /*---------------response params----------------*/

    public Headers responseHeaders;

    public int responseCode;

    public String responseMessage;

    public long responseContentLength;

    public String responseBody;


    public void logOut() {
        Logger.LogPrinter logPrinter = Logger.createLogPrinter(Logger.INFO).tag("HttpInfo Logout")
        .appendLine("url: " + url)
        .appendLine("protocol: %s,  method: %s", protocol, method)
        .appendLine("request took time: %d ms", tookMills)
        .appendLine("response code: %d,  message: %s", responseCode, responseMessage)
        .appendLine("----------request-----------")
        .appendLine("Headers:");
        for (String headerName : requestHeaders.names()) {
            logPrinter.appendLine("%s : %s", headerName, requestHeaders.get(headerName));
        }
        logPrinter.appendLine("Body:")
        .appendLine(requestBody)
        .appendLine("----------response----------")
        .appendLine("Headers:");
        for (String headerName : responseHeaders.names()) {
            logPrinter.appendLine("%s : %s", headerName, responseHeaders.get(headerName));
        }
        logPrinter.appendLine("Body:")
        .appendLine(responseBody);

        logPrinter.print();
    }

    @Override
    public String toString() {
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
                '}';
    }
}
