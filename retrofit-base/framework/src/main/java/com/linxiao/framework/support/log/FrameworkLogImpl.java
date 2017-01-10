package com.linxiao.framework.support.log;

import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;

/**
 * 框架下Log实现
 * Created by LinXiao on 2017-01-05.
 */
public class FrameworkLogImpl implements LogInterface {

    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;

    private static final String SEPARATE = "───────────────────────────────────────────────────────────────";
    private static final int MAX_LINE_LENGTH = SEPARATE.length() * 2;
    public static String TAG = "APPTAG";

    @Override
    public void v(String tag, String log) {
        logOutput(VERBOSE, tag, log);
    }

    @Override
    public void d(String tag, String log) {
        logOutput(DEBUG, tag, log);
    }

    @Override
    public void i(String tag, String log) {
        logOutput(INFO, tag, log);
    }

    @Override
    public void w(String tag, String log) {
        logOutput(WARNING, tag, log);
    }

    @Override
    public void e(String tag, String log) {
        logOutput(ERROR, tag, log);
    }

    @Override
    public void e(String tag, Throwable e) {
        logOutput(ERROR, tag, Log.getStackTraceString(e));
    }


    private synchronized void logOutput(int logType, String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        printLog(logType, SEPARATE);
        printLog(logType, String.format("Application: %s | Thread: %s | Tag: %s",
                BaseApplication.getApplicationName(),
                Thread.currentThread().getName(),
                tag));
        printLog(logType, SEPARATE);

        int lineStart = 0;
        int lineEnd = 0;
        for (int i = 0; i < log.length(); i++) {
            if (log.charAt(i) == '\n' || lineEnd >= MAX_LINE_LENGTH || i == log.length() - 1) {
                printLog(logType, log.substring(lineStart, i));
                lineStart = i;
                lineEnd = 0;
            }
            else {
                lineEnd++;
            }
        }

        printLog(logType, SEPARATE);
    }


    private void printLog(int logType, String line) {
        switch (logType) {
            case VERBOSE:
                Log.v(TAG, line);
                break;
            case DEBUG :
                Log.d(TAG, line);
                break;
            case INFO :
                Log.i(TAG, line);
                break;
            case WARNING:
                Log.w(TAG, line);
                break;
            case ERROR :
                Log.e(TAG, line);
                break;
            default:
                break;
        }
    }

}
