package com.linxiao.framework.net;

import com.linxiao.framework.common.GsonParser;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;
/**
 * flatMap function to parse data in ApiResponse to data object
 *
 * @author linxiao
 * @since 2018-08-20
 */
public class ApiResponseParseFunction<T> implements Function<ApiResponse, T> {

    private Type type = Object.class;

    public ApiResponseParseFunction() {
    }

    public ApiResponseParseFunction(Class<T> clazz) {
        type = clazz;
    }

    public ApiResponseParseFunction(Type type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T apply(ApiResponse apiResponse) throws Exception {
        T ret;
        if (!apiResponse.isSuccess()) {
            throw new ApiResponse.ApiException(apiResponse);
        }
        ret = GsonParser.getParser().fromJson(apiResponse.data, type);
        if (ret == null) {
            if (type == Object.class) {
                return (T) apiResponse;
            }
            throw new ApiResponse.ApiException(apiResponse);
        }
        return ret;
    }
}