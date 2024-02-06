package com.linxiao.framework.architecture;

/**
 * base data manager class
 * Created by linxiao on 2016-11-24.
 */
public abstract class BaseDataManager {

    protected String TAG;

    public BaseDataManager() {
        TAG = this.getClass().getSimpleName();
    }
}
