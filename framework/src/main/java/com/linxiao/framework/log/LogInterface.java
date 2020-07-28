package com.linxiao.framework.log;

/**
 * Log接口，用于切换不同的Log实现
 * Created by linxiao on 2017-01-05.
 */
interface LogInterface {

    void v(String tag, String log);

    void d(String tag, String log);

    void i(String tag, String log);

    void w(String tag, String log);

    void e(String tag, String log);

    void e(String tag, Throwable e);
}
