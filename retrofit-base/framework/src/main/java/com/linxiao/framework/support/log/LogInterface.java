package com.linxiao.framework.support.log;

/**
 * Log接口，用于切换不同的Log实现
 * Created by LinXiao on 2017-01-05.
 */
public interface LogInterface {

    void v(String tag, String log);

    void d(String tag, String log);

    void i(String tag, String log);

    void w(String tag, String log);

    void e(String tag, String log);

    void e(String tag, Throwable e);
}
