package com.linxiao.framework.common;

import com.linxiao.framework.net.ApiException;
import com.linxiao.framework.permission.PermissionException;

import java.net.UnknownHostException;

import retrofit2.HttpException;

public class ErrorMessageUtil {

    protected String getMessageString(Throwable e) {
        if (e instanceof ApiException) {
            return e.getMessage();
        }
        if (e instanceof UnknownHostException) {
            return "unknown host";
        }
        if (e instanceof HttpException) {
            return "http error(" + ((HttpException) e).code() + ")";
        }
        if (e instanceof PermissionException) {
            return "permission denied";
        }
        return e.getMessage();
    }
}
