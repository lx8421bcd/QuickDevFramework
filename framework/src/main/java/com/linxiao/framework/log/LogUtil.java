package com.linxiao.framework.log;

import android.util.Log;

import com.linxiao.framework.common.ApplicationUtil;

/**
 * Log tools
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2019-12-09
 */
public class LogUtil {

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

}
