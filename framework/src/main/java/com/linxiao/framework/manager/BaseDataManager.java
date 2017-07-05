package com.linxiao.framework.manager;

/**
 * base data manager class
 * Created by LinXiao on 2016-11-24.
 */
public abstract class BaseDataManager {

    protected String TAG;

    public BaseDataManager() {
        TAG = this.getClass().getSimpleName();
    }
    
}
