package com.linxiao.framework.net

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

/**
 * framework retrofit response convert rules define
 *
 *
 * conversion follows the rules below:
 * 1. if defined response type is [okhttp3.ResponseBody],
 * converter will return [okhttp3.ResponseBody] object directly <br></br>
 *
 * 2. if defined response type is [ApiResponse],
 * converter will convert response to [ApiResponse]
 * and return its instance <br></br>
 *
 * 3. if conversion is failed or conversion result is null or
 * result of method [SuccessChecker.isSuccess] ()} is false,
 * converter will return a [ApiException] <br></br>
 *
 * 4. other response type definitions will be treated as definitions of
 * response data, converter will call [ApiResponse.getParsedData] ()}
 * method to get converted response data and return,
 * empty converted value will cause [ApiException]
 *
 *
 * @author lx8421bcd
 * @since 2016-08-09
 */
@Suppress("UNCHECKED_CAST")
class ApiConverterFactory(
    private val gson: Gson,
    private val responseChecker: ApiResponseChecker,
    private val responseParser: ApiResponseParser,
    private val successChecker: SuccessChecker,
) : Converter.Factory() {

    fun interface ApiResponseChecker {
        fun checkIsApiResponse(responseString: String): Boolean
    }

    fun interface ApiResponseParser {
        fun parse(responseString: String): ApiResponse
    }

    fun interface SuccessChecker {
        fun isSuccess(apiResponse: ApiResponse): Boolean
    }

    private val apiResponseCallbackList: MutableList<(response: ApiResponse) -> Unit> = ArrayList()

    fun addOnApiResponseInterceptCallback(callback: (response: ApiResponse) -> Unit) {
        if (!apiResponseCallbackList.contains(callback)) {
            apiResponseCallbackList.add(callback)
        }
    }

    fun removeOnApiResponseInterceptCallback(callback: (response: ApiResponse) -> Unit) {
        apiResponseCallbackList.remove(callback)
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return ApiResponseConverter<Any>(gson, type)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    private class GsonRequestBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
    ) : Converter<T, RequestBody> {

        val mediaType = "application/json; charset=UTF-8".toMediaType()
        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer: Writer = OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8)
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            return buffer.readByteString().toRequestBody(mediaType)
        }
    }

    private inner class ApiResponseConverter<T>(
        private val gson: Gson,
        private val type: Type
    ) : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.string()
            if (type == ResponseBody::class.java) {
                return gson.fromJson(response, type)
            }
            // 非标准接口Response数据直接返回接口声明类型
            if (!responseChecker.checkIsApiResponse(response)) {
                return gson.fromJson(response, type)
            }
            // 标准接口Response数据：{"code": number, "message": string, "data":object}
            // 先解析成ApiResponse再向上层返回body
            val apiResponse = responseParser.parse(response)
            for (callback in apiResponseCallbackList) {
                callback.invoke(apiResponse)
            }
            if (!successChecker.isSuccess(apiResponse)) {
                throw ApiException(apiResponse)
            }
            // 如果声明要求直接返回ApiResponse类型则直接向上层返回ApiResponse类型
            if (type == ApiResponse::class.java) {
                return apiResponse as T
            }
            val body = apiResponse.getParsedData<Any>(type)
            // 在body解析为空的时候的处理，处理不需要解析返回值的接口调用，
            // 以及应对文档给出body为“{}”却返回“[]”等情况
            if (body == null) {
                if (object : TypeToken<JSONObject?>() {}.type == type) {
                    return JSONObject() as T
                }
                if (object : TypeToken<JSONArray?>() {}.type == type) {
                    return JSONArray() as T
                }
                throw IOException(
                    "expected [${type.javaClass.getSimpleName()}] body but got null, response: $apiResponse"
                )
            }
            return body as T
        }

    }

}