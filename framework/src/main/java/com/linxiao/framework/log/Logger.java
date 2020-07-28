package com.linxiao.framework.log;

import android.util.Log;

import com.linxiao.framework.common.ApplicationUtil;

/**
 * 框架Log管理类
 * Created by linxiao on 2017-01-05.
 */
public class Logger {

    private static boolean verboseEnabled = true;
    private static boolean debugEnabled = true;
    private static boolean infoEnabled = true;
    private static boolean logEnabled = true;
    private static LogInterface logImpl = new SimpleLogImpl();

    private Logger() {}

    /**
     * print current thread name
     * @param TAG TAG
     */
    public static void currentThread(String TAG) {
        Log.i(TAG, "current thread: " + Thread.currentThread().getName());
    }

    /**
     * print current process pid
     * @param TAG TAG
     */
    public static void currentProcess(String TAG) {
        Log.i(TAG, "current process:" + ApplicationUtil.getProcessName(android.os.Process.myPid()));
    }

    /**
     * print stack trace
     * @param TAG TAG
     */
    public static void printStackTrace(String TAG) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            sb.append(element.toString());
        }
        Log.i(TAG, sb.toString());
    }

    /**
     * 设置是否输出Log信息
     * @param logEnabled 是否打开Log
     * */
    public static void setLogEnabled(boolean logEnabled) {
        Logger.logEnabled = logEnabled;
    }

    public static void setVerboseEnabled(boolean verboseEnabled) {
        Logger.verboseEnabled = verboseEnabled;
    }

    public static void setDebugEnabled(boolean debugEnabled) {
        Logger.debugEnabled = debugEnabled;
    }

    public static void setInfoEnabled(boolean infoEnabled) {
        Logger.infoEnabled = infoEnabled;
    }

    public static void v(String tag, String message) {
        if (verboseEnabled && logEnabled) {
            logImpl.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (debugEnabled && logEnabled) {
            logImpl.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (infoEnabled && logEnabled) {
            logImpl.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (logEnabled) {
            logImpl.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (logEnabled) {
            logImpl.e(tag, message);
        }
    }

    public static void e(String tag, Throwable e) {
        if (logEnabled) {
            logImpl.e(tag, e);
        }
    }
}
