package com.linxiao.framework.support.log;

/**
 * Log接口，用于切换不同的Log实现
 * Created by LinXiao on 2017-01-05.
 */
public interface LogInterface {

    void v(String log);

    void d(String log);

    void i(String log);

    void w(String log);

    void e(String log);
}
