package com.linxiao.framework.common

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.ObjectConstructor
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

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

    /**
     * get GsonBuilder that contains default deserialize rules
     *
     * @return instance of GsonBuilder
     */
    val builder: GsonBuilder
        get() {
            return GsonBuilder()
                .registerTypeAdapterFactory(NullableTypeAdapterFactory())
                .registerTypeAdapter(Int::class.javaPrimitiveType, IntegerDeserializer())
                .registerTypeAdapter(Int::class.java, IntegerDeserializer())
                .registerTypeAdapter(String::class.java, StringDeserializer())
                .registerTypeAdapter(JSONObject::class.java, JSONObjectDeserializer())
                .registerTypeAdapter(JSONArray::class.java, JSONArrayDeserializer())
        }

    /**
     * get default gson instance
     * @return instance of Gson
     */
    @JvmStatic
    val parser: Gson by lazy {
        builder.create()
    }

    /**
     * cast json string to defined type
     * 将传入json string反序列化为声明类型对象
     *
     * @param json 需要反序列化的json string
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
    </T> */
    @JvmStatic
    fun <T> fromJSONObject(json: String?, clazz: Class<T>?): T? {
        try {
            return parser.fromJson(json, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * cast [JSONObject] to defined type
     * 将传入[JSONObject]对象反序列化为声明类型对象
     *
     * @param json 需要反序列化的[JSONObject]对象
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
    </T> */
    fun <T> fromJSONObject(json: JSONObject?, clazz: Class<T>?): T? {
        if (json == null) {
            return null
        }
        try {
            return parser.fromJson(json.toString(), clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * cast [JSONArray] string to the list of defined type
     * 将传入[JSONArray]字符串反序列化为声明类型对象
     *
     * @param jsonArrayStr 需要反序列化的[JSONObject]字符串
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
    </T> */
    fun <T> fromJSONArray(jsonArrayStr: String?, clazz: Class<T>?): List<T> {
        val retList: MutableList<T> = ArrayList()
        if (TextUtils.isEmpty(jsonArrayStr)) {
            return retList
        }
        try {
            val arr = JsonParser.parseString(jsonArrayStr).asJsonArray
            for (jsonElement in arr) {
                retList.add(parser.fromJson(jsonElement, clazz))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return retList
        }
        return retList
    }

    /**
     * cast [JSONArray] object to the list of defined type
     * 将传入[JSONArray]对象反序列化为声明类型对象
     *
     * @param jsonArray 需要反序列化的[JSONObject]对象
     * @param clazz 需要反序列化的对象类型class
     * @param <T> 需要反序列化的对象类型声明
     * @return 反序列化类型的对象，如果失败则返回null
    </T> */
    fun <T> fromJSONArray(jsonArray: JSONArray, clazz: Class<T>?): List<T> {
        return fromJSONArray(jsonArray.toString(), clazz)
    }

    /**
     * cast defined type to [JSONObject]
     * 将传入类型对象序列化为[JSONObject]对象
     *
     * @param data 需要序列化的对象
     * @param <T> 需要序列化的对象类型声明
     * @return [JSONObject]对象，如果序列化失败则返回null
    </T> */
    @JvmStatic
    fun <T> toJSONObject(data: T): JSONObject? {
        try {
            val jsonText = parser.toJson(data)
            return JSONObject(jsonText)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * cast defined type to [JSONArray]
     * 将传入类型对象序列化为[JSONArray]对象
     *
     * @param data 需要序列化的对象列表
     * @param <T> 需要序列化的对象类型声明
     * @return [JSONArray]对象，如果序列化失败则返回null
    </T> */
    fun <T> toJSONArray(data: List<T>?): JSONArray? {
        try {
            val jsonText = parser.toJson(data)
            return JSONArray(jsonText)
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

    /**
     * kotlin null-safety check TypeAdapter
     * <p>
     * execute null check during json deserialize, if a value declared be non-null but deserialized
     * a null value, the adapter will turned it to the default value that object declared
     * </p>
     */
    class NullableTypeAdapterFactory : TypeAdapterFactory {

        override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val field = gson.javaClass.getDeclaredField("constructorConstructor")
            field.isAccessible = true
            val constructorConstructor = field.get(gson) as ConstructorConstructor
            val constructor = constructorConstructor.get(type)

            val delegate = gson.getDelegateAdapter(this, type)
            // If the class isn't kotlin, don't use the custom type adapter
            if (type.rawType.declaredAnnotations.none { it.annotationClass.qualifiedName == "kotlin.Metadata" }) {
                return null
            }
            return NullSafeTypeAdapter(type, delegate, constructor)
        }

        class NullSafeTypeAdapter<T>(
            val type: TypeToken<T>,
            val delegate: TypeAdapter<T>,
            val constructor: ObjectConstructor<T>
        ) : TypeAdapter<T>() {

            override fun write(out: JsonWriter, value: T?) = delegate.write(out, value)

            override fun read(input: JsonReader): T? {
                val value = delegate.read(input) ?: return null
                val defaultValue = constructor.construct() ?: null
                val kotlinClass: KClass<Any> = Reflection.createKotlinClass(type.rawType)
                // Ensure none of its non-nullable fields were deserialized to null
                kotlinClass.memberProperties.forEach {
                    if (!it.isLateinit && !it.returnType.isMarkedNullable && it.get(value) == null) {
                        if (defaultValue == null) {
                            throw JsonParseException("Value of non-nullable member [${it.name}] cannot be null")
                        }
                        val field = value::class.java.getDeclaredField(it.name)
                        field.isAccessible = true
                        field.set(value, it.get(defaultValue))
                    }
                }
                return value
            }
        }
    }

}