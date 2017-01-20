package com.linxiao.framework.net;

import android.support.annotation.NonNull;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Http请求信息拦截器，在Retrofit build的过程中添加
 * Created by linxiao on 2016/12/4.
 */
public class HttpInfoCatchInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private HttpInfoCatchListener httpInfoCatchListener;

    private boolean catchEnabled;

    /**
     * 是否抓取Http请求信息
     * */
    public void setCatchEnabled(boolean enabled) {
        catchEnabled = enabled;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!catchEnabled || httpInfoCatchListener == null) {
            return chain.proceed(request);
        }
        HttpInfoEntity entity = new HttpInfoEntity();

        RequestBody requestBody = request.body();
        Connection connection = chain.connection();

        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        entity.protocol = protocol.toString();
        entity.method = request.method();
        entity.url = request.url().toString();
        entity.requestHeaders = request.headers();

        if (requestBody != null) {
            entity.requestContentType = requestBody.contentType().toString();
            entity.requestContentLength = requestBody.contentLength();
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            entity.requestBody = buffer.readString(charset);
        }
        //-----request prepare----
        long startNs = System.nanoTime();
        Response response;
        response = chain.proceed(request);
        //-----request done--------

        entity.tookMills = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        entity.responseHeaders = response.headers();
        entity.responseCode = response.code();
        entity.responseMessage = response.message();
        entity.responseContentLength = responseBody.contentLength();
        if (response.body() != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire requestBody.
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    entity.responseBody = "unreadable";
                }
            }
            if (isPlaintext(buffer) && responseBody.contentLength() > 0) {
                entity.responseBody = buffer.clone().readString(charset);
            }
            else {
                entity.responseBody = "unreadable";
            }
        }
        httpInfoCatchListener.onInfoCaught(entity);
        return response;
    }

    /**
     * 检查是否包含可以读取的字符信息
     * */
    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    public HttpInfoCatchListener getHttpInfoCatchListener() {
        return httpInfoCatchListener;
    }

    public void setHttpInfoCatchListener(@NonNull HttpInfoCatchListener httpInfoCatchListener) {
        this.httpInfoCatchListener = httpInfoCatchListener;
    }
}
