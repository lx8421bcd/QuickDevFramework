package com.linxiao.framework.common

import com.linxiao.framework.common.ErrorMessageUtil.ErrorMessageParser
import com.linxiao.framework.permission.PermissionException
import retrofit2.HttpException
import java.net.UnknownHostException

object ErrorMessageUtil {

    fun interface ErrorMessageParser<T> {
        fun parseMessage(e: T): String?
    }

    private val errorMessageMap = HashMap<Class<out Throwable>, ErrorMessageParser<Throwable>>()

    init {
        setErrorMessageParser(UnknownHostException::class.java) {
            return@setErrorMessageParser "unknown host"
        }
        setErrorMessageParser(HttpException::class.java) {
            return@setErrorMessageParser "http error(" + (it as HttpException).code() + ")"
        }
        setErrorMessageParser(PermissionException::class.java) {
            return@setErrorMessageParser "permission denied"
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> setErrorMessageParser(clazz: Class<T>, parser: ErrorMessageParser<T>?) {
        errorMessageMap[clazz] = ErrorMessageParser {
            return@ErrorMessageParser parser?.parseMessage(it as T)
        }
    }

    @JvmStatic
    fun Throwable.parsedMessage(): String {
        return errorMessageMap[this.javaClass]?.parseMessage(this)
            ?: this.message
            ?: this.javaClass.simpleName
    }
}
