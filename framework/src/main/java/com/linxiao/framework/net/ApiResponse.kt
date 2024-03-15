package com.linxiao.framework.net

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.linxiao.framework.json.GsonParser.parser
import org.json.JSONException
import org.json.JSONObject
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

    val isSuccess: Boolean
        get() = code == businessSuccessCode

    fun <T> getResponseData(clazz: Class<T>?): T? {
        try {
            return parser.fromJson(data, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> getResponseData(t: Type?): T? {
        try {
            return parser.fromJson(data, t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        /**
         * 业务请求成功code
         */
        var businessSuccessCode = 0
        @JvmStatic
        fun isApiResponseString(responseString: String): Boolean {
            if (TextUtils.isEmpty(responseString)) {
                return false
            }
            if (!(responseString.startsWith("{") && responseString.endsWith("}"))) {
                return false
            }
            try {
                val respObj = JSONObject(responseString)
                return respObj.has("code") &&
                        (respObj.has("desc") || respObj.has("message") || respObj.has("msg")) &&
                        (respObj.has("body") || respObj.has("data"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return false
        }
    }
}
