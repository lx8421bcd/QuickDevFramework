package com.linxiao.framework.log;

import android.util.Log;

/**
 * 简易log实现，可以在不需要详细Log信息的时候比如正式环境下使用,以提升性能
 * Created by linxiao on 2017-01-08.
 */
class SimpleLogImpl implements LogInterface {

    private static final int MAX_LINE_LENGTH = 3500;

    interface  LogExecutor {
        void log(String tag, String msg);
    }

    LogExecutor logV = Log::v;
    LogExecutor logD = Log::d;
    LogExecutor logI = Log::i;
    LogExecutor logW = Log::w;
    LogExecutor logE = Log::e;

    @Override
    public void v(String tag, String log) {
        logLine(tag, log, logV);
    }

    @Override
    public void d(String tag, String log) {
        logLine(tag, log, logD);
    }

    @Override
    public void i(String tag, String log) {
        logLine(tag, log, logI);
    }

    @Override
    public void w(String tag, String log) {
        logLine(tag, log, logW);
    }

    @Override
    public void e(String tag, String log) {
        logLine(tag, log, logE);
    }

    @Override
    public void e(String tag, Throwable e) {
        logLine(tag, Log.getStackTraceString(e), logE);
    }

    private void logLine(String tag, String message, LogExecutor executor) {
        if (message == null) {
            executor.log(tag, "null");
            return;
        }
        if (message.length() < MAX_LINE_LENGTH) {
            executor.log(tag, message);
            return;
        }
        int subStart = 0;
        int subEnd = MAX_LINE_LENGTH;
        while(subEnd < message.length()) {
            executor.log(tag, message.substring(subStart, subEnd));
            subStart = subEnd;
            subEnd += MAX_LINE_LENGTH;
        }
        executor.log(tag, message.substring(subStart));
    }
}
