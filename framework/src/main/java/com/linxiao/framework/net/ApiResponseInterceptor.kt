package com.linxiao.framework.net;

import android.util.Log;

import androidx.annotation.NonNull;

import com.linxiao.framework.json.GsonParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * ApiResponse拦截器, 用于进行全局错误拦截处理
 *
 * @author lx8421bcd
 * @since 2022-12-07
 */
public class ApiResponseInterceptor implements Interceptor {

    private static final String TAG = ApiResponseInterceptor.class.getSimpleName();

    public interface OnApiResponseInterceptCallback {

        void onApiResponse(ApiResponse response);
    }

    private final List<OnApiResponseInterceptCallback> apiResponseCallbackList = new ArrayList<>();

    public void addOnApiResponseInterceptCallback(OnApiResponseInterceptCallback callback) {
        if (!apiResponseCallbackList.contains(callback)) {
            apiResponseCallbackList.add(callback);
        }
    }

    public void removeOnApiResponseInterceptCallback(OnApiResponseInterceptCallback callback) {
        apiResponseCallbackList.remove(callback);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response;
        response = chain.proceed(chain.request());

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            Log.e(TAG, "intercept: null response body");
            return response;
        }
        try {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire requestBody.
            Buffer buffer = source.buffer();
            // handle gzip
            if ("gzip".equalsIgnoreCase(response.headers().get("Content-Encoding"))) {
                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                }
            }
            if (!isPlaintext(buffer) || responseBody.contentLength() == 0) {
                return response;
            }
            MediaType contentType = responseBody.contentType();
            Charset charset = contentType == null ? StandardCharsets.UTF_8 : contentType.charset(StandardCharsets.UTF_8);
            String responseString = charset == null ? "" :  buffer.clone().readString(charset);
            if (ApiResponse.isApiResponseString(responseString)) {
                ApiResponse apiResponse = GsonParser.fromJSONObject(responseString, ApiResponse.class);
                if (apiResponse != null) {
                    for (OnApiResponseInterceptCallback callback : apiResponseCallbackList) {
                        callback.onApiResponse(apiResponse);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
