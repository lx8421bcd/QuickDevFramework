package com.linxiao.framework.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Global context holder for whole project
 * <p> class description </p>
 *
 * @author linxiao
 * Create on 2018/6/11.
 */
public final class GlobalContext {
    
    @SuppressLint("StaticFieldLeak")
    private static Context mAppContext;
    
    public static Context get() {
        if (mAppContext == null) {
            mAppContext = getAppContext();
        }
        return mAppContext;
    }
    
    /**
     * 设置全局级别的Context，在Application初始化时调用
     * @param context Application的Context
     */
    public static void setGlobalContext(Context context) {
        mAppContext = context;
    }
    
    /**
     * 通过反射获取AppContext，避免直接使用static context
     */
    @SuppressLint("PrivateApi")
    private static Context getAppContext() {
        Application application = null;
        try {
            application = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application;
    }
}
