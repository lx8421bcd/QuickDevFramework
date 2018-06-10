package com.linxiao.framework.common;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * a wrapper for gson, handled most problems that could cause fatal exception
 * <p>
 * using gson to cast some non standard json string, like defined int but got a double,
 * or you want get a force cast a jsonobject to string, using standard gson may cause exceptions
 * like 'expected something but was another...'.
 *
 * this wrapper is using to handle such situations like this,
 * you can use it to do some force cast and do not need to worry about the crash,
 * such like force cast to String, {@link JSONObject} and {@link JSONArray}
 *
 * if you want to add more deserialize rules based on this gson template,
 * you can use {@link #getBuilder()} method and add your custom deserializer
 * or just edit this class if your rules are acting on the whole project
 *
 * Gson包装工具，添加了处理常见崩溃情况的反序列化规则，也支持反序列化为{@link JSONObject}和{@link JSONArray}。
 * 如需添加自定义规则，请使用{@link #getBuilder()}方法获取Builder对象并自行添加，或者直接修改此class
 * </p>
 *
 * @author linxiao
 * Create on 2018/1/11.
 */
public final class GsonParser {
    
    private static final String TAG = GsonParser.class.getSimpleName();
    
    private static Gson gson;
    
    /**
     * get GsonBuilder that contains default deserialize rules
     *
     * @return instance of GsonBuilder
     */
    public static GsonBuilder getBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(int.class, new IntegerDeserializer())
        .registerTypeAdapter(Integer.class, new IntegerDeserializer())
        .registerTypeAdapter(String.class, new StringDeserializer())
        .registerTypeAdapter(JSONObject.class, new JSONObjectDeserializer())
        .registerTypeAdapter(JSONArray.class, new JSONArrayDeserializer());
        return builder;
    }
    
    /**
     * get default gson instance
     * @return instance of Gson
     */
    public static Gson getParser() {
        if (gson == null) {
            gson = getBuilder().create();
        }
        return gson;
    }
    
    /**
     * cast json string to defined type
     * 将传入json string反序列化为声明类型对象
     *
     * @param json 需要反序列化的json string
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
     */
    public static <T> T fromJSONObject(String json, Class<T> clazz) {
        try {
            return getParser().fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * cast {@link JSONObject} to defined type
     * 将传入{@link JSONObject}对象反序列化为声明类型对象
     *
     * @param json 需要反序列化的{@link JSONObject}对象
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
     */
    public static <T> T fromJSONObject(JSONObject json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return getParser().fromJson(json.toString(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * cast {@link JSONArray} string to the list of defined type
     * 将传入{@link JSONArray}字符串反序列化为声明类型对象
     *
     * @param jsonArrayStr 需要反序列化的{@link JSONObject}字符串
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
     */
    public static <T> List<T> fromJSONArray(String jsonArrayStr, Class<T> clazz) {
        List<T> retList = new ArrayList<>();
        if (TextUtils.isEmpty(jsonArrayStr)) {
            return retList;
        }
        try {
            JsonArray arr = new JsonParser().parse(jsonArrayStr).getAsJsonArray();
            for (JsonElement jsonElement : arr) {
                retList.add(getParser().fromJson(jsonElement, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return retList;
        }
        return retList;
    }
    
    /**
     * cast {@link JSONArray} object to the list of defined type
     * 将传入{@link JSONArray}对象反序列化为声明类型对象
     *
     * @param jsonArray 需要反序列化的{@link JSONObject}对象
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
     */
    public static <T> List<T>  fromJSONArray(JSONArray jsonArray, Class<T> clazz) {
        return fromJSONArray(String.valueOf(jsonArray), clazz);
    }
    
    /**
     * cast defined type to {@link JSONObject}
     * 将传入类型对象序列化为{@link JSONObject}对象
     *
     * @param data 需要序列化的对象
     * @param <T> 需要序列化的对象类型声明
     * @return {@link JSONObject}对象，如果序列化失败则返回null
     */
    public static <T> JSONObject toJSONObject(T data) {
        try {
            String jsonText = getParser().toJson(data);
            return new JSONObject(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * cast defined type to {@link JSONArray}
     * 将传入类型对象序列化为{@link JSONArray}对象
     *
     * @param data 需要序列化的对象列表
     * @param <T> 需要序列化的对象类型声明
     * @return {@link JSONArray}对象，如果序列化失败则返回null
     */
    public static <T> JSONArray toJSONArray(List<T> data) {
        
        try {
            String jsonText = getParser().toJson(data);
            return new JSONArray(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Rules for the situation that defined as integer but get float,
     * force cast to integer
     */
    static class IntegerDeserializer implements JsonDeserializer<Integer> {
        
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsInt();
            }
            catch (Exception e) {
                try {
                    return (int) json.getAsDouble();
                } catch (Exception e1) {
                    throw new JsonParseException(e.getMessage());
                }
            }
        }
    }
    
    /**
     * if defined string, force cast to string whatever it got
     */
    static class StringDeserializer implements JsonDeserializer<String> {
        
        @Override
        public String deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsString();
            }
            catch (Exception e) {
                return json.toString();
            }
        }
    }
    
    /**
     * method to auto deserialize data as {@link JSONObject}
     */
    static class JSONObjectDeserializer implements JsonDeserializer<JSONObject> {
        
        @Override
        public JSONObject deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context) throws JsonParseException {
            try {
                return new JSONObject(json.toString());
            } catch (JSONException e) {
                Log.w(TAG, "err, string = " + json.toString());
            }
            return null;
        }
    }
    
    /**
     * method to auto deserialize data as {@link JSONArray}
     */
    static class JSONArrayDeserializer implements JsonDeserializer<JSONArray> {
        
        @Override
        public JSONArray deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
            try {
                return new JSONArray(json.toString());
            } catch (JSONException e) {
                Log.w(TAG, "err, string = " + json.toString());
            }
            return null;
        }
    }
    
}
