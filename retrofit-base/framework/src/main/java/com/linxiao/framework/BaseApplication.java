package com.linxiao.framework;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.linxiao.framework.event.ExitAppEvent;
import com.linxiao.framework.support.log.LogManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 应用Application基类
 * <p>用于提供Framewrok模块下Application相关的基础功能，以及为Framework层提供Application Context</p>
 * Created by LinXiao on 2016-11-24.
 */
public abstract class BaseApplication extends Application {
    protected static String TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = getClass().getSimpleName();

    }

    /**
     * 通过广播的形式退出应用
     */
    public static void exitApplication() {
        EventBus.getDefault().post(new ExitAppEvent());
    }

    /**
     * 通过反射获取AppContext，避免直接使用static context
     */
    public static Context getAppContext() {
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

    /**
     * 获取应用名称
     * <p>框架层无法直接获取应用层的String资源，因此在框架需要应用名称的时候可以通过此方法获取</p>
     *
     * @return String 应用名称
     */
    public static String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = getAppContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getAppContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    /**
     * 获取应用图标Drawable资源
     * */
    @Nullable
    public static Drawable getApplicationIcon() {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = getAppContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getAppContext().getPackageName(), 0);
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用版本号
     * */
    @Nullable
    public static String getApplicatinVersion() {
        PackageManager packageManager;
        PackageInfo packageInfo;
        try {
            packageManager = getAppContext().getPackageManager();
            packageInfo = packageManager.getPackageInfo(getAppContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用签名
     * */
    @Nullable
    public static String getAppSignature() {
        PackageManager packageManager;
        PackageInfo packageInfo;
        try {
            packageManager = getAppContext().getPackageManager();
            packageInfo = packageManager.getPackageInfo(getAppContext().getPackageName(), 0);
            return packageInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查应用主进程是否正在运行
     * */
    public static boolean isMainProcessRunning() {
        Context mContext = getAppContext();
        String packageName = mContext.getPackageName();
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfo.size(); i++){
            if(processInfo.get(i).processName.equals(packageName)){
                LogManager.i(TAG, String.format("the %s is running", packageName));
                return true;
            }
        }
        LogManager.i(TAG, String.format("the %s is not running", packageName));
        return false;
    }

}
