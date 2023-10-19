package com.linxiao.framework.net;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.linxiao.framework.json.GsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * entity data responses from server
 *
 * @author lx8421bcd
 * @since 2016-07-27
 */
public class ApiResponse {

    public static class ApiException extends IOException {

        private final ApiResponse response;
        public ApiException(ApiResponse response) {
            super("(" + response.code +")");
            this.response = response;
        }

        public ApiResponse getResponse() {
            return response;
        }
    }

    /**
     * 业务请求成功code
     */
    public static int businessSuccessCode = 0;

    @SerializedName(value = "code", alternate = {"code"})
    public int code;

    @SerializedName(value = "desc", alternate = {"message", "msg"})
    public String message;

    @SerializedName(value = "body", alternate = {"data"})
    public String data;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public static boolean isApiResponseString(String responseString) {
        if (TextUtils.isEmpty(responseString)) {
            return false;
        }
        if (!(responseString.startsWith("{") && responseString.endsWith("}"))) {
            return false;
        }
        try {
            JSONObject respObj = new JSONObject(responseString);
            return  respObj.has("code") &&
                    (respObj.has("desc") || respObj.has("message") || respObj.has("msg")) &&
                    (respObj.has("body") || respObj.has("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSuccess() {
        return code == businessSuccessCode;
    }

    public <T> T getResponseData(Class<T> clazz) {
        try {
            return GsonParser.getParser().fromJson(data, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public <T> T getResponseData(Type t) {
        try {
            return GsonParser.getParser().fromJson(data, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

