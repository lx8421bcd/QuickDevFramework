package com.linxiao.framework

import com.linxiao.framework.common.ErrorMessageUtil
import com.linxiao.framework.common.ErrorMessageUtil.parsedMessage
import com.linxiao.framework.net.ApiException
import com.linxiao.framework.permission.PermissionException
import com.linxiao.framework.widget.LoadingView
import retrofit2.HttpException
import java.net.UnknownHostException

object FrameworkConfigs {
    fun init() {
        ErrorMessageUtil.setErrorMessageParser(UnknownHostException::class.java) {
            return@setErrorMessageParser "unknown host"
        }
        ErrorMessageUtil.setErrorMessageParser(HttpException::class.java) {
            return@setErrorMessageParser "http error(" + (it as HttpException).code() + ")"
        }
        ErrorMessageUtil.setErrorMessageParser(PermissionException::class.java) {
            return@setErrorMessageParser "permission denied"
        }
        ErrorMessageUtil.setErrorMessageParser(ApiException::class.java) {
            return@setErrorMessageParser it.response.message ?: "(${it.response.code})"
        }

        LoadingView.defaultExceptionParser = LoadingView.ExceptionParser {
            return@ExceptionParser it.parsedMessage()
        }

    }

}