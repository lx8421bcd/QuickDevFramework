package com.linxiao.framework.support.log;

import android.support.annotation.NonNull;

/**
 * 框架Log管理类
 * Created by LinXiao on 2017-01-05.
 */
public class Logger {

    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;

    private static boolean logEnabled = true;
    private static int logLevel = VERBOSE;

    private static LogInterface logImpl = new FrameworkLogImpl();

    private Logger() {}

    /**
     * 设置是否输出Log信息
     * @param logEnabled 是否打开Log
     * */
    public static void setLogEnabled(boolean logEnabled) {
        Logger.logEnabled = logEnabled;

    }

    /**
     * 设置Log输出等级
     * <p>输出等级为ERROR > WARNING > INFO > DEBUG > VERBOSE ;
     * 设定一个等级后则只会输出大于等于此等级的内容，默认为VERBOSE</p>
     * @param logLevel Log等级
     * */
    public static void setLogLevel(int logLevel) {
        if (logLevel >= VERBOSE && logLevel <= ERROR) {
            Logger.logLevel = logLevel;
        }
    }

    /**
     * 设置Log具体实现，直接决定框架内Log输出形式
     * @param logImpl LogInterface接口实现类对象
     * */
    public static void setLogInterface(@NonNull LogInterface logImpl) {
        Logger.logImpl = logImpl;

    }

    /**
     * 创建一个logPrinter
     * <p>在需要多行log集合在一起输出时使用</p>
     * */
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

        public LogPrinter tag(String tag) {
            this.tag = tag;
            return this;
        }

        public LogPrinter appendLine(String message) {
            logStrBuilder.append(message).append('\n');
            return this;
        }

        public LogPrinter appendLine(String formatStr, Object... params) {
            logStrBuilder.append(String.format(formatStr, params)).append('\n');
            return this;
        }

        public void print() {
            String message = logStrBuilder.toString();
            switch (logType) {
            case VERBOSE:
                Logger.v(tag, message);
                break;
            case DEBUG :
                Logger.d(tag, message);
                break;
            case INFO :
                Logger.i(tag, message);
                break;
            case WARNING:
                Logger.w(tag, message);
                break;
            case ERROR :
                Logger.e(tag, message);
                break;
            default:
                break;
            }
        }
    }

}
