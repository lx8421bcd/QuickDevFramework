package com.linxiao.framework.net;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

/**
 * 接口调用返回异常
 * Created by linxiao on 2016-07-27.
 */
public class ApiException extends IOException {

    @SerializedName("code")
    public int code;
    @SerializedName("desc")
    public String message;
    @SerializedName("data")
    public String data;

    private ApiResponse apiResponse;

    public ApiException(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
        this.code = apiResponse.code;
        this.message = apiResponse.message;
        this.data = apiResponse.data;
    }

    public ApiException(int code, String message) {
        super("ApiException code = " + code + ", message = " + message);
        this.code = code;
        this.message = message;

    }

    @Override
    public String getMessage() {
        if(message != null) {
            return message;
        }
        return super.getMessage();
    }

    public ApiResponse getResponseBody() {
        if (apiResponse == null) {
            apiResponse = new ApiResponse();
            apiResponse.code = code;
            apiResponse.message = message;
            apiResponse.data = data;
        }
        return apiResponse;
    }
}
