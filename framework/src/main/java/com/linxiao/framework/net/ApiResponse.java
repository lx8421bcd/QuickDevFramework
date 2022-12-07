package com.linxiao.framework.net;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.linxiao.framework.common.GsonParser;

import java.lang.reflect.Type;

/**
 * entity data responses from server
 * <p>
 * this is a usual structure sample, you can change it to
 * any structure you need, just don't forget to modify deserialize method
 * in the {@link GsonDeserializer}
 * </p>
 *
 * Created by linxiao on 2016-07-27.
 */
public class ApiResponse {

    private static final int SUCCESS = 200;
    public static final String SERIALIZED_KEY_CODE = "code";
    public static final String SERIALIZED_KEY_DESC = "desc";
    public static final String SERIALIZED_KEY_BODY = "body";

    @SerializedName(SERIALIZED_KEY_CODE)
    public int code;

    @SerializedName(SERIALIZED_KEY_DESC)
    public String message;

    @SerializedName(SERIALIZED_KEY_BODY)
    public String data;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public boolean success() {
        return code == SUCCESS;
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

    /**
     * ApiResponse Deserializer
     * <p>
     * used to customize response deserialize rules
     * </p>
     */
    public static class GsonDeserializer implements JsonDeserializer<ApiResponse> {
        @Override
        public ApiResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ApiResponse response = new ApiResponse();
            try {
                response.code = obj.get(SERIALIZED_KEY_CODE).getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
                response.code = -1;
            }
            try {
                JsonElement msgObj = obj.get(SERIALIZED_KEY_DESC);
                if (msgObj != null) {
                    response.message = msgObj.getAsString();
                }
                else {
                    response.message = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.message = "";
            }
            try {
                response.data = String.valueOf(obj.get(SERIALIZED_KEY_BODY));
            } catch (Exception e) {
                e.printStackTrace();
                response.data = "";
            }
            return response;
        }
    }
}

