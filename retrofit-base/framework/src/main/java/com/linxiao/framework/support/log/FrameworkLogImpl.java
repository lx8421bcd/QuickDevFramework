package com.linxiao.framework.support.log;

import android.util.Log;

/**
 * 框架下Log实现
 * Created by LinXiao on 2017-01-05.
 */
public class FrameworkLogImpl implements LogInterface {

    @Override
    public void v(String tag, String log) {
        Log.v(tag, log);
    }

    @Override
    public void d(String tag, String log) {
        Log.d(tag, log);
    }

    @Override
    public void i(String tag, String log) {
        Log.i(tag, log);
    }

    @Override
    public void w(String tag, String log) {
        Log.w(tag, log);
    }

    @Override
    public void e(String tag, String log) {
        Log.e(tag, log);
    }

}
