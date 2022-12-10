package com.linxiao.framework.net;

import com.google.gson.reflect.TypeToken;
import com.linxiao.framework.common.GsonParser;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
/**
 * flatMap function to parse data in ApiResponse to data object
 * <p> usage：.flatMap&lt new ApiResponseParseFunction() &gt </p>
 *
 * @author linxiao
 * create on 2018-08-20
 */
public class ApiResponseParseFunction<T> implements Function<ApiResponse, ObservableSource<T>> {

    private Type type = Object.class;

    public ApiResponseParseFunction() {
    }

    public ApiResponseParseFunction(int businessSuccessCode, Class<T> clazz) {
        type = clazz;
    }
    public ApiResponseParseFunction(int businessSuccessCode, TypeToken<T> token) {
        type = token.getType();
    }
    @Override
    public ObservableSource<T> apply(ApiResponse apiResponse) {
        T ret;
        if (apiResponse.isSuccess()) {
            return Observable.error(new ApiResponse.ApiException(apiResponse));
        }
        try {
            ret = GsonParser.getParser().fromJson(apiResponse.data, type);
        } catch (Exception e) {
            return Observable.error(e);
        }
        if (ret == null) {
            return Observable.error(new ApiResponse.ApiException(apiResponse));
        }
        return Observable.just(ret);
    }
}