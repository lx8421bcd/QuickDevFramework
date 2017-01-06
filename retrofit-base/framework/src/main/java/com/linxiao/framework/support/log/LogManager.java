package com.linxiao.framework.support.log;

import android.support.annotation.NonNull;

/**
 * 框架Log管理类
 * Created by LinXiao on 2017-01-05.
 */
public class LogManager {

    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;

    private static boolean logEnabled = true;
    private static int logLevel = VERBOSE;

    private static LogInterface logImpl;


    public static void v(String message) {
        if (logLevel <= VERBOSE && logEnabled) {
            logImpl.v(message);
        }

    }

    public static void d(String message) {
        if (logLevel <= DEBUG && logEnabled) {
            logImpl.d(message);
        }
    }

    public static void i(String message) {
        if (logLevel <= INFO && logEnabled) {
            logImpl.i(message);
        }
    }

    public static void w(String message) {
        if (logLevel <= WARNING && logEnabled) {
            logImpl.w(message);
        }
    }

    public static void e(String message) {
        if (logLevel <= ERROR && logEnabled) {
            logImpl.e(message);
        }

    }

    public static void setLogImpl(@NonNull LogInterface logImpl) {
        LogManager.logImpl = logImpl;
    }

    public static void setLogLevel(int logLevel) {
        LogManager.logLevel = logLevel;
    }

    public static void setLogEnabled(boolean logEnabled) {
        LogManager.logEnabled = logEnabled;
    }
}
