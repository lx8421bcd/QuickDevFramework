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

    private static LogInterface logImpl = new FrameworkLogImpl();

    /**
     * 设置是否输出Log信息
     * @param logEnabled 是否打开Log
     * */
    public static void setLogEnabled(boolean logEnabled) {
        LogManager.logEnabled = logEnabled;
    }

    /**
     * 设置Log输出等级
     * <p>输出等级为ERROR > WARNING > INFO > DEBUG > VERBOSE ;
     * 设定一个等级后则只会输出大于等于此等级的内容，默认为VERBOSE</p>
     * @param logLevel Log等级
     * */
    public static void setLogLevel(int logLevel) {
        if (logLevel >= VERBOSE && logLevel <= ERROR) {
            LogManager.logLevel = logLevel;
        }
    }

    /**
     * 设置Log具体实现，直接决定框架内Log输出形式
     * @param logImpl LogInterface接口实现类对象
     * */
    public static void setLogInterface(@NonNull LogInterface logImpl) {
        LogManager.logImpl = logImpl;
    }

    public static LogPrinter createLogPrinter(int logType) {
        return new LogPrinter(logType);
    }

    public static void v(String tag, String message) {
        if (logLevel <= VERBOSE && logEnabled) {
            logImpl.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (logLevel <= DEBUG && logEnabled) {
            logImpl.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (logLevel <= INFO && logEnabled) {
            logImpl.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (logLevel <= WARNING && logEnabled) {
            logImpl.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (logLevel <= ERROR && logEnabled) {
            logImpl.e(tag, message);
        }
    }

    public static void e(String tag, Throwable e) {
        if (logLevel <= ERROR && logEnabled) {
            logImpl.e(tag, e);
        }
    }

    public static class LogPrinter {
        StringBuilder logStrBuilder;
        int logType;
        String tag;

        public LogPrinter(int logType) {
            this.logType = logType;
            logStrBuilder = new StringBuilder();
        }

        public LogPrinter Tag(String tag) {
            this.tag = tag;
            return this;
        }

        public LogPrinter append(String message) {
            logStrBuilder.append(message).append('\n');
            return this;
        }

        public LogPrinter append(String formatStr, Object... params) {
            logStrBuilder.append(String.format(formatStr, params)).append('\n');
            return this;
        }

        public void print() {
            String message = logStrBuilder.toString();
            switch (logType) {
            case VERBOSE:
                LogManager.v(tag, message);
                break;
            case DEBUG :
                LogManager.d(tag, message);
                break;
            case INFO :
                LogManager.i(tag, message);
                break;
            case WARNING:
                LogManager.w(tag, message);
                break;
            case ERROR :
                LogManager.e(tag, message);
                break;
            default:
                break;
            }
        }
    }

}
