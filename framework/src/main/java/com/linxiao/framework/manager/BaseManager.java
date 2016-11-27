package com.linxiao.framework.manager;

/**
 *
 * Created by LinXiao on 2016-11-24.
 */

public abstract class BaseManager {
    protected static String TAG;

    public BaseManager() {
        TAG = this.getClass().getSimpleName();
    }
}
