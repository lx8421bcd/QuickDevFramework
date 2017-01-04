package com.linxiao.framework.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求测速拦截器，在Retrofit build的过程中添加
 * Created by linxiao on 2017/1/4.
 */
public class HttpLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        long startNs = System.nanoTime();

        Response response;

        response = chain.proceed(request);

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        return null;
    }

}
