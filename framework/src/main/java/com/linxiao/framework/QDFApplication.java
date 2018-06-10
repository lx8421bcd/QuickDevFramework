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
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.log.Logger;
import com.linxiao.framework.common.ToastAlert;


import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 应用Application基类
 * <p>用于提供Framework模块下Application相关的基础功能，以及为Framework层提供Application Context</p>
 * Created by linxiao on 2016-11-24.
 */
public abstract class QDFApplication extends Application {
    protected static String TAG;

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
        ToastAlert.init(this);
    }
    
    /**
     * 从配置资源获取字符串
     * */
    public static String getResString(@StringRes int resId) {
        Context context = getAppContext();
        if (context == null) {
            return null;
        }
        return context.getResources().getString(resId);
    }

    /**
     * 通过广播的形式退出应用
     */
    public static void exitApplication() {
        Intent exitIntent = new Intent();
        exitIntent.setAction(BaseActivity.ACTION_EXIT_APPLICATION);
        QDFApplication.getAppContext().sendBroadcast(exitIntent);
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
     * 获取应用版本
     * */
    @Nullable
    public static String getApplicationVersionName() {
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
     * 获取应用版本代号
     * */
    public static int getApplicationVersionCode() {
        PackageManager packageManager;
        PackageInfo packageInfo;
        try {
            packageManager = getAppContext().getPackageManager();
            packageInfo = packageManager.getPackageInfo(getAppContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
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
     * 检查App是否处于前台
     * */
    public static boolean isAppForeground() {
        Context context = getAppContext();
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (!appProcess.processName.equals(packageName)) {
                continue;
            }
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 系统转屏是否开启
     * */
    public static boolean isSystemOrientationEnabled() {
        return Settings.System.getInt(getAppContext().getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }
    
    /**
     * 获取系统开机时间
     * */
    public static long getSystemBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }
    
    /**
     * 获取正在运行的Activity的数量
     * */
    public static int getRunningActivityCount() {
        
        return mActivityCount;
    }

    private class FrameworkActivityLifeCycleCallback implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            mActivityCount++;
        }

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityCount--;
        }
    }

}
