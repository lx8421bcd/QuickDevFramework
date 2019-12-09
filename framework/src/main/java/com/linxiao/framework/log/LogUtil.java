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

    public static void currentThread(String TAG) {
        Log.i(TAG, "current thread: " + Thread.currentThread().getName());
    }

    public static void currentProcess(String TAG) {
        Log.i(TAG, "current process:" + ApplicationUtil.getProcessName(android.os.Process.myPid()));
    }

    public static void printStackTrace(String TAG) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            sb.append(element.toString());
        }
        Log.i(TAG, sb.toString());
    }

}
