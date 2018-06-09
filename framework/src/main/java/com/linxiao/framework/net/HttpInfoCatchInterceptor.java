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
import okio.GzipSource;

/**
 * instance of OkHttp interceptor
 * <p>
 * Used to catch http request and response info and
 * save as {@link HttpInfoEntity}.
 *
 * Use method {@link okhttp3.OkHttpClient.Builder#addNetworkInterceptor(Interceptor)} to register
 * {@link HttpInfoCatchInterceptor} instance into OkHttpClient, if registered instance using
 * {@link okhttp3.OkHttpClient.Builder#addInterceptor(Interceptor)},
 * you will unable to get complete network messages.
 *
 * </p>
 *
 * Created by linxiao on 2016/12/4.
 */
public class HttpInfoCatchInterceptor implements Interceptor {
    
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    private HttpInfoCatchListener httpInfoCatchListener;
    
    private boolean catchEnabled;
    
    /**
     * set catch http info enabled
     * @param enabled enabled
     */
    public void setCatchEnabled(boolean enabled) {
        catchEnabled = enabled;
    }
    
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
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
            entity.requestContentType = String.valueOf(requestBody.contentType());
            entity.requestContentLength = requestBody.contentLength();
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (charset != null) {
                entity.requestBody = buffer.readString(charset);
            }
        }
        //-----request prepare----
        long startNs = System.nanoTime();
        Response response;
        response = chain.proceed(request);
        //-----request done--------
        
        entity.tookMills = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        entity.responseHeaders = response.headers();
        entity.responseCode = response.code();
        entity.responseMessage = response.message();
        
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            entity.responseContentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire requestBody.
            Buffer buffer = source.buffer();
            
            // handle gzip
            if ("gzip".equalsIgnoreCase(response.headers().get("Content-Encoding"))) {
                GzipSource gzippedResponseBody = null;
                try {
                    gzippedResponseBody = new GzipSource(buffer.clone());
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                } finally {
                    if (gzippedResponseBody != null) {
                        gzippedResponseBody.close();
                    }
                }
            }
            
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    entity.responseBody = "unreadable, charset error";
                }
            }
            if (isPlaintext(buffer) && responseBody.contentLength() != 0) {
                if (charset != null) {
                    entity.responseBody = buffer.clone().readString(charset);
                }
            }
            else {
                entity.responseBody = "unreadable, not text";
            }
        }
        httpInfoCatchListener.onInfoCaught(entity);
        return response;
    }
    
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
