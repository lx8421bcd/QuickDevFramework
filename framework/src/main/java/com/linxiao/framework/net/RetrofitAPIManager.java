package com.linxiao.framework.net;

import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *
 * Created by LinXiao on 2016-11-27.
 */
public class RetrofitAPIManager {

    private static final String TAG = RetrofitAPIManager.class.getSimpleName();

    public RetrofitAPIBuilder buildClientAPI() {
        return new RetrofitAPIBuilder();
    }


    public static class RetrofitAPIBuilder {

        private Retrofit.Builder mRetrofitBuilder;
        private ArrayList<Interceptor> interceptors;

        public RetrofitAPIBuilder() {
            mRetrofitBuilder = new Retrofit.Builder();
            interceptors = new ArrayList<>();
        }

        public RetrofitAPIBuilder setServerUrl(String serverUrl) {
            mRetrofitBuilder.baseUrl(serverUrl);
            return this;
        }

        public RetrofitAPIBuilder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public RetrofitAPIBuilder setOkHttpClient(OkHttpClient okHttpClient) {
            mRetrofitBuilder.client(okHttpClient);
            return this;
        }

        public RetrofitAPIBuilder addCallAdapterFactory(CallAdapter.Factory factory) {
            mRetrofitBuilder.addCallAdapterFactory(factory);
            return this;
        }

        public RetrofitAPIBuilder addConvertFactory(Converter.Factory factory) {
            mRetrofitBuilder.addConverterFactory(factory);
            return this;
        }

        public <T> T  build(Class<T> clazzClientApi) {
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            for (Interceptor interceptor : interceptors) {
//                builder.addInterceptor(interceptor);
//            }
//            OkHttpClient okHttpClient = builder.build();
//            mRetrofitBuilder.client(okHttpClient);

            return mRetrofitBuilder.build().create(clazzClientApi);
        }
    }

}
