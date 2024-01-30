package com.linxiao.framework.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * 应用全局级别context提供类
 * <p> 为所有应用模块提供Application Context，
 * static field的生命周期与Application一致，无需担心内存泄漏问题
 * </p>
 *
 * @author linxiao
 * @since 2016-11-30
 */
public final class ContextProvider {

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
