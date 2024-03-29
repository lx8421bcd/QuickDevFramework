package com.linxiao.framework.common

import com.linxiao.framework.permission.PermissionException
import retrofit2.HttpException
import java.net.UnknownHostException

object ErrorMessageUtil {

    fun interface ErrorMessageParser {
        fun parseMessage(e: Throwable?): String?
    }

    private val errorMessageMap = HashMap<Class<out Throwable>, ErrorMessageParser>()

    init {
        errorMessageMap[UnknownHostException::class.java] = ErrorMessageParser { e: Throwable? ->
            return@ErrorMessageParser "unknown host"
        }
        errorMessageMap[HttpException::class.java] = ErrorMessageParser { e: Throwable? ->
            return@ErrorMessageParser "http error(" + (e as HttpException).code() + ")"
        }
        errorMessageMap[PermissionException::class.java] = ErrorMessageParser { e: Throwable? ->
            return@ErrorMessageParser "permission denied"
        }
    }

    @JvmStatic
    fun getMessageString(e: Throwable): String {
        val parser = errorMessageMap[e.javaClass]
        return if (parser != null) {
            parser.parseMessage(e) ?: ""
        } else e.message ?: ""
    }

    @JvmStatic
    fun setErrorMessageParser(clazz: Class<out Throwable>, parser: ErrorMessageParser?) {
        if (parser == null) {
            return
        }
        errorMessageMap[clazz] = parser
    }
}
