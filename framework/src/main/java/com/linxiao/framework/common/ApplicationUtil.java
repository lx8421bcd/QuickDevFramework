package com.linxiao.framework.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.linxiao.framework.permission.PermissionManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * application util methods
 *
 * <p>
 * method collections of application and android os,
 * </p>
 * @author linxiao
 * @since 2016-12-12.
 */
public class ApplicationUtil {

    private static final String TAG = ApplicationUtil.class.getSimpleName();

    private ApplicationUtil() {}

    /**
     * check this application is foreground
     * <p>
     * application is visible to user on screen and screen is not lock,
     * which means there must have least one activity is resumed state.
     * </p>
     */
    public static boolean isAppForeground() {
        Context context = ContextProvider.get();
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
     * close application
     * <p>
     * Stop all active activities and call Runtime.exit() after 500ms
     * </p>
     * @param activity activity instance
     */
    public static void exitApplication(Activity activity) {
        if (activity != null) {
            activity.finishAffinity();
        }
        new Handler().postDelayed(() -> Runtime.getRuntime().exit(0), 500);
    }

    /**
     * restart application
     *
     * <p>
     * Stop all active activities and launch application again with default launch intent.
     * Start activity from background without permission is not allowed since Android Q,
     * AlarmManager method won't work without grant permission
     * </p>
     * @param activity activity instance
     */
    public static void restartApplication(Activity activity) {
        if (activity != null) {
            activity.finishAffinity();
        }
        String packageName = ContextProvider.get().getPackageName();
        Intent mStartActivity = ContextProvider.get().getPackageManager().getLaunchIntentForPackage(packageName);
        if (mStartActivity == null) {
            return;
        }
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextProvider.get().startActivity(mStartActivity);
    }

    /**
     * open application detail page of this app in Settings
     */
    public static void openAppDetail(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    /**
     * is system system orientation enabled
     */
    public static boolean isSystemOrientationEnabled() {
        return Settings.System.getInt(ContextProvider.get().getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    /**
     * get system boot time as millisecond unit
     *
     * @return system running time since last boot, unit ms
     */
    public static long getSystemBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }


    /**
     * get ApplicationInfo Object by package name
     * @param packageName package name
     * @return {@link ApplicationInfo} object
     */
    public static ApplicationInfo getAppInfo(String packageName) {
        try {
            PackageManager packageManager = ContextProvider.get().getPackageManager();
            return packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get Application PackageInfo Object by package name
     * @param packageName package name
     * @return {@link PackageInfo} object
     */
    public static PackageInfo getPackageInfo(String packageName) {
        try {
            PackageManager packageManager = ContextProvider.get().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get application name by package name
     *
     * @param packageName package name
     * @return app name
     */
    public static String getAppName(String packageName) {
        try {
            PackageManager packageManager = ContextProvider.get().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get application name by package name
     *
     * @param packageName package name
     * @return app icon
     */
    public static Drawable getAppIcon(String packageName) {
        try {
            PackageManager packageManager = ContextProvider.get().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get process name by pid
     * @param pid pid
     * @return process name
     */
    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) ContextProvider.get().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * check application is running by package name
     *
     * @param packageName package name
     * @return boolean
     */
    public static boolean isAppRunning(String packageName) {
        ActivityManager activityManager = (ActivityManager) ContextProvider.get().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfo.size(); i++) {
            if (processInfo.get(i).processName.equals(packageName)) {
                Log.d(TAG, String.format("the %s is running", packageName));
                return true;
            }
        }
        Log.d(TAG, String.format("the %s is not running", packageName));
        return false;
    }
    
    /**
     * check application is installed by package name
     *
     * @param packageName package name
     * @return boolean
     */
    public static boolean isAppInstalled(String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = ContextProvider.get().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    /**
     * install application with apk
     * <p>
     * please check package install permission is granted before install apk
     * or installation will not complete
     * </p>
     *
     * @param filePath full path of install apk file
     */
    public static void installApk(String filePath, String providerAuth) {
        Context mContext = ContextProvider.get();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mContext.getPackageManager().canRequestPackageInstalls()) {
            return; // do not have package install permission
        }
        File installFile = new File(filePath);
        if (!installFile.exists()) {
            Log.e(TAG, "cannot find install apk file");
            return;
        }
        if (!filePath.endsWith(".apk")) {
            Log.e(TAG, "target file is not apk file");
            return;
        }
        Uri installPackageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installPackageUri = FileProvider.getUriForFile(mContext, providerAuth, installFile);
        }
        else {
            installPackageUri = Uri.fromFile(new File(filePath)); //Uri.parse("file://" + installFile);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(installPackageUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

    /**
     * 获取手机cpu信息
     */
    public static String getCPUName() {
        FileReader fr = null;
        BufferedReader br = null;
        String text;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            while ((text = br.readLine()) != null) {
                if (text.toLowerCase().contains("hardware")) {
                    String[] array = text.split(":\\s+", 2);
                    return array[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }
}
