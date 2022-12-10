package com.linxiao.framework.net;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.linxiao.framework.common.GsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * framework retrofit response convert rules define
 * <p>
 * conversion follows the rules below:
 * 1. if defined response type is {@link okhttp3.ResponseBody},
 *    converter will return {@link okhttp3.ResponseBody} object directly <br/>
 *
 * 2. if defined response type is {@link ApiResponse},
 *    converter will convert response to {@link ApiResponse}
 *    and return its instance <br/>
 *
 * 3. if conversion is failed or conversion result is null or
 *    result of method {@link ApiResponse#isSuccess()} ()} is false,
 *    converter will return a {@link ApiResponse.ApiException} <br/>
 *
 * 4. other response type definitions will be treated as definitions of
 *    response data, converter will call {@link ApiResponse#getResponseData(Type)} ()}
 *    method to get converted response data and return,
 *    empty converted value will cause {@link ApiResponse.ApiException}
 *
 * </p>
 * Created by linxiao on 2016-08-09.
 */
public class ApiConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static ApiConverterFactory create() {
        ApiConverterFactory factory = new ApiConverterFactory(GsonParser.getParser());
        return factory;
    }

    private ApiConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            @NonNull Type type,
            @NonNull Annotation[] annotations,
            @NonNull Retrofit retrofit
    ) {
        return new ApiResponseConverter<>(gson, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            @NonNull Type type,
            @NonNull Annotation[] parameterAnnotations,
            @NonNull Annotation[] methodAnnotations,
            @NonNull Retrofit retrofit
    ) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    private static final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(@NonNull T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    static class ApiResponseConverter<T> implements Converter<ResponseBody, T> {

        private final Gson gson;
        private final Type type;

        ApiResponseConverter(Gson gson, Type type) {
            this.gson = gson;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T convert(@NonNull ResponseBody value) throws IOException {
            String response = value.string();
            if (type.equals(ResponseBody.class)) {
                return gson.fromJson(response, type);
            }
            // 非标准接口Response数据直接返回接口声明类型
            if (!ApiResponse.isApiResponseString(response)) {
                return gson.fromJson(response, type);
            }
            // 标准接口Response数据：{"code": number, "message": string, "data":object}
            // 先解析成ApiResponse再向上层返回body
            ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);
            if (apiResponse == null) {
                throw new IOException("response parse failed, response: " + response);
            }
            if (!apiResponse.isSuccess()) {
                throw new ApiResponse.ApiException(apiResponse);
            }
            // 如果声明要求直接返回ApiResponse类型则直接向上层返回ApiResponse类型
            if (type.equals(ApiResponse.class)) {
                return (T) apiResponse;
            }
            Object body =  apiResponse.getResponseData(type);
            // 在body解析为空的时候的处理，处理不需要解析返回值的接口调用，
            // 以及应对文档给出body为“{}”却返回“[]”等情况
            if (body == null) {
                if (new TypeToken<JSONObject>(){}.getType().equals(type)) {
                    return (T) new JSONObject();
                }
                if (new TypeToken<JSONArray>(){}.getType().equals(type)) {
                    return (T) new JSONArray();
                }
                throw new IOException("expected [" + type.getClass().getSimpleName() + "] " +
                        "body but got null, response: " + apiResponse);
            }
            return (T) body;
        }

    }
}