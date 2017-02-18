package com.linxiao.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.log.Logger;


import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

/**
 * 应用Application基类
 * <p>用于提供Framework模块下Application相关的基础功能，以及为Framework层提供Application Context</p>
 * Created by LinXiao on 2016-11-24.
 */
public abstract class BaseApplication extends Application {
    protected static String TAG;

    /**
     * 用于判断一个app是否处于前台
     */
    private static int mForegroundCount = 0;

    /**
     * 应用内Activity的数量，如果数量为0，则可以判断当前应用未启动
     * <p>如果有什么一像素Activity等东西存在请另改值</p>
     * */
    private static int mActivityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = getClass().getSimpleName();
        this.registerActivityLifecycleCallbacks(new FrameworkActivityLifeCycleCallback());

    }

    /**
     * 通过广播的形式退出应用
     */
    public static void exitApplication() {
        Intent exitIntent = new Intent();
        exitIntent.setAction(BaseActivity.ACTION_EXIT_APPLICATION);
        BaseApplication.getAppContext().sendBroadcast(exitIntent);
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
    public static String getApplicationVersion() {
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
            packageInfo = packageManager.getPackageInfo(getAppContext().getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signature  = packageInfo.signatures[0];

            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            return pubKey + "|" + signNumber;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.d(TAG, "package name not found");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查应用主进程是否正在运行
     * */
    public static boolean isAppRunning() {
        Context mContext = getAppContext();
        String packageName = mContext.getPackageName();
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfo.size(); i++){
            if(processInfo.get(i).processName.equals(packageName)){
                Logger.i(TAG, String.format(Locale.getDefault(),
                        "process %s is running, activity count = %d", packageName, mActivityCount));
                //如果有没被销毁的Activity，则App至少处于后台正在运行，否则App应处于未运行状态
                return mActivityCount > 0;
            }
        }
        Logger.i(TAG, String.format("the %s is not running", packageName));
        return false;
    }

    /**
     * 检查App是否处于前台
     * */
    public static boolean isAppForeground() {
        return isAppRunning() && mForegroundCount > 0;
    }

    private class FrameworkActivityLifeCycleCallback implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            mActivityCount++;
        }

        @Override
        public void onActivityStarted(Activity activity) {
            mForegroundCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {
            mForegroundCount--;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityCount--;
        }
    }

}
