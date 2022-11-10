package com.linxiao.framework.net;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
/**
 * flatMap function to parse data in ApiResponse to data object
 * <p> usage：.flatMap&lt new JsonObjectParseFunction() &gt </p>
 *
 * @author linxiao
 * create on 2018-08-20
 */
public class ApiResponseParseFunction<T> implements Function<ApiResponse, ObservableSource<T>> {
    @Override
    public ObservableSource<T> apply(ApiResponse apiResponse) throws Exception {
        T obj = apiResponse.getResponseData(new TypeToken<List<T>>() {}.getType());
        if (obj == null) {
            return Observable.error(new IOException("data parse error"));
        }
        return Observable.just(obj);
    }
}