package com.linxiao.framework.json

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.linxiao.framework.json.GsonParser.ParserConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.Collections

/**
 * a wrapper for gson, handled most problems that could cause fatal exception
 *
 *
 * using gson to cast some non standard json string, like defined int but got a double,
 * or you want get a force cast a jsonobject to string, using standard gson may cause exceptions
 * like 'expected something but was another...'.
 *
 * this wrapper is using to handle such situations like this,
 * you can use it to do some force cast and do not need to worry about the crash,
 * such like force cast to String, [JSONObject] and [JSONArray]
 *
 * if you want to add more deserialize rules based on this gson template,
 * you can use [.getBuilder] method and add your custom deserializer
 * or just edit this class if your rules are acting on the whole project
 *
 * Gson包装工具，添加了处理常见崩溃情况的反序列化规则，也支持反序列化为[JSONObject]和[JSONArray]。
 * 如需添加自定义规则，请使用[.getBuilder]方法获取Builder对象并自行添加，或者直接修改此class
 *
 *
 * @author linxiao
 * @since 2018/1/11.
 */
object GsonParser {

    private val TAG = GsonParser::class.java.simpleName

    fun interface ParserConfig {
        fun onConfigParser(builder: GsonBuilder)
    }

    @JvmStatic
    var extraParserConfigs = ParserConfig {}
        set(value) {
            field = value
            parser = builder.buildNullSafeParser()
        }

    /**
     * get GsonBuilder that contains default deserialize rules
     *
     * @return instance of GsonBuilder
     */
    val builder: GsonBuilder
        get() {
            val builder = GsonBuilder()
                .registerTypeAdapter(Int::class.javaPrimitiveType, IntegerDeserializer())
                .registerTypeAdapter(Int::class.java, IntegerDeserializer())
                .registerTypeAdapter(String::class.java, StringDeserializer())
                .registerTypeAdapter(JSONObject::class.java, JSONObjectDeserializer())
                .registerTypeAdapter(JSONArray::class.java, JSONArrayDeserializer())
            extraParserConfigs.onConfigParser(builder)
            return builder
        }

    /**
     * get default gson instance
     * @return instance of Gson
     */
    @JvmStatic
    var parser: Gson = builder.buildNullSafeParser()

    @JvmStatic
    fun GsonBuilder.buildNullSafeParser(): Gson {
        return this.create().also {
            var field = it.javaClass.getDeclaredField("factories")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val factoryList = ArrayList(field.get(it) as List<TypeAdapterFactory>)
            field = it.javaClass.getDeclaredField("constructorConstructor")
            field.isAccessible = true
            val constructorConstructor = field.get(it) as ConstructorConstructor

            field = it.javaClass.getDeclaredField("jsonAdapterFactory")
            field.isAccessible = true
            val jsonAdapterFactory = field.get(it) as JsonAdapterAnnotationTypeAdapterFactory
            factoryList.removeAt(factoryList.size - 1)
            factoryList.add(
                KotlinReflectiveTypeAdapterFactory(
                    constructorConstructor,
                    FieldNamingPolicy.IDENTITY,
                    Excluder.DEFAULT,
                    jsonAdapterFactory,
                    mutableListOf()
                )
            )
            field = it.javaClass.getDeclaredField("factories")
            field.isAccessible = true

            field.set(it, Collections.unmodifiableList(factoryList))
        }
    }


    inline fun <reified T> String?.parseAsJson(): T? {
        this ?: return null
        try {
            return parser.fromJson(this, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    inline fun <reified T> JSONObject?.parse(): T? {
        this ?: return null
        try {
            return parser.fromJson(this.toString(), object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    inline fun <reified T> JSONArray?.parse(): List<T>? {
        this ?: return null
        try {
            return parser.fromJson(this.toString(), object : TypeToken<List<T>>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun Any?.toJson(): String? {
        this ?: return null
        try {
            return parser.toJson(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Rules for the situation that defined as integer but get float,
     * force cast to integer
     */
    internal class IntegerDeserializer : JsonDeserializer<Int> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement, typeOfT: Type,
            context: JsonDeserializationContext
        ): Int {
            return try {
                json.asInt
            } catch (e: Exception) {
                try {
                    json.asDouble.toInt()
                } catch (e1: Exception) {
                    throw JsonParseException(e.message)
                }
            }
        }
    }

    /**
     * if defined string, force cast to string whatever it got
     */
    internal class StringDeserializer : JsonDeserializer<String> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement, typeOfT: Type,
            context: JsonDeserializationContext
        ): String {
            return try {
                json.asString
            } catch (e: Exception) {
                json.toString()
            }
        }
    }

    /**
     * method to auto deserialize data as [JSONObject]
     */
    internal class JSONObjectDeserializer : JsonDeserializer<JSONObject?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement, typeOfT: Type,
            context: JsonDeserializationContext
        ): JSONObject? {
            try {
                return JSONObject(json.toString())
            } catch (e: JSONException) {
                Log.w(TAG, "err, string = $json")
            }
            return null
        }
    }

    /**
     * method to auto deserialize data as [JSONArray]
     */
    internal class JSONArrayDeserializer : JsonDeserializer<JSONArray?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement, typeOfT: Type,
            context: JsonDeserializationContext
        ): JSONArray? {
            try {
                return JSONArray(json.toString())
            } catch (e: JSONException) {
                Log.w(TAG, "err, string = $json")
            }
            return null
        }
    }

}