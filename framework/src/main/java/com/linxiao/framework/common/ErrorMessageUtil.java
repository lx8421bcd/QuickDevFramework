package com.linxiao.framework.common;

import com.linxiao.framework.permission.PermissionException;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit2.HttpException;

public class ErrorMessageUtil {

    public interface ErrorMessageParser {
        String parseMessage(Throwable e);
    }

    private static final HashMap<Class<? extends Throwable>, ErrorMessageParser> errorMessageMap = new HashMap<>();

    static {
        errorMessageMap.put(UnknownHostException.class, e -> {
            return "unknown host";
        });
        errorMessageMap.put(HttpException.class, e -> {
            return "http error(" + ((HttpException)e).code() + ")";
        });
        errorMessageMap.put(PermissionException.class, e -> {
            return "permission denied";
        });
    }
    public static String getMessageString(Throwable e) {
        ErrorMessageParser parser = errorMessageMap.get(e.getClass());
        if (parser != null) {
            return parser.parseMessage(e);
        }
        return e.getMessage();
    }

    private static void setErrorMessageParser(Class<? extends Throwable> clazz, ErrorMessageParser parser) {
        if (parser == null) {
            return;
        }
        errorMessageMap.put(clazz, parser);
    }
}
