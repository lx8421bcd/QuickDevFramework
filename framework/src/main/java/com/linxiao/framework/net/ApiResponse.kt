package com.linxiao.framework.net

import com.google.gson.annotations.SerializedName
import com.linxiao.framework.json.GsonParser.parser
import java.lang.reflect.Type

/**
 * entity data responses from server
 *
 * @author lx8421bcd
 * @since 2016-07-27
 */
class ApiResponse {
    @SerializedName(value = "code", alternate = ["code"])
    var code = 0

    @SerializedName(value = "desc", alternate = ["message", "msg"])
    var message: String? = null

    @JvmField
    @SerializedName(value = "body", alternate = ["data"])
    var data: String? = null
    override fun toString(): String {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}'
    }

    fun <T> getParsedData(clazz: Class<T>?): T? {
        try {
            return parser.fromJson(data, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> getParsedData(t: Type?): T? {
        try {
            return parser.fromJson(data, t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
