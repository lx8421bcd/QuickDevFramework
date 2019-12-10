package com.linxiao.framework.common;

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
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.linxiao.framework.architecture.BaseActivity;

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
 * 系统相关工具类
 * Created by linxiao on 2016/12/12.
 */
public class ApplicationUtil {

    private static final String TAG = ApplicationUtil.class.getSimpleName();

    private ApplicationUtil() {}

    /**
     * 通过广播的形式退出应用
     */
    public static void exitApplication() {
        Intent exitIntent = new Intent();
        exitIntent.setAction(BaseActivity.ACTION_EXIT_APPLICATION);
        ContextProvider.get().sendBroadcast(exitIntent);
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    public static void restartApplication() {

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
            packageManager = ContextProvider.get().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ContextProvider.get().getPackageName(), 0);
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
            packageManager = ContextProvider.get().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ContextProvider.get().getPackageName(), 0);
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
            packageManager = ContextProvider.get().getPackageManager();
            packageInfo = packageManager.getPackageInfo(ContextProvider.get().getPackageName(), 0);
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
            packageManager = ContextProvider.get().getPackageManager();
            packageInfo = packageManager.getPackageInfo(ContextProvider.get().getPackageName(), 0);
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
            packageManager = ContextProvider.get().getPackageManager();
            packageInfo = packageManager.getPackageInfo(ContextProvider.get().getPackageName(), PackageManager.GET_SIGNATURES);
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
            Log.d(TAG, "package name not found");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查App是否处于前台
     * */
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
     * 系统转屏是否开启
     * */
    public static boolean isSystemOrientationEnabled() {
        return Settings.System.getInt(ContextProvider.get().getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    /**
     * 获取系统开机时间
     * */
    public static long getSystemBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    /**
     * 获取进程名称
     * @param pid
     * @return
     */
    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) ContextProvider.get().getSystemService(Context.ACTIVITY_SERVICE);
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
     * 判断应用是否已经启动
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isProcessRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

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
     * 跳转至应用详情
     * <p>可用于在用户完全禁止动态权限弹出后跳转至应用详情页面提示用户打开权限</p>
     *
     * @param context     一个context
     * */
    public static void jumpToApplicationDetail(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }
    
    /**
     * 检查应用是否安装
     * */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    /**
     * 安装应用
     *
     * */
    public static void installApk(Context context, String apkPath) {
        File installFile = new File(apkPath);
        if (!installFile.exists()) {
            Log.e(TAG, "cannot found apk file, path = " + apkPath);
            return;
        }
        if (!apkPath.endsWith(".apk")) {
            Log.e(TAG, "illegal file : " + apkPath);
            return;
        }
        Uri installPackageUri = Uri.parse("file://" + installFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(installPackageUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
     * 获取手机参数信息
     * @return mobile device params string
     */
    public static String getHandSetInfo() {
        return "手机型号:" + Build.MODEL
                + "\n系统版本:" + Build.VERSION.RELEASE
                + "\n产品型号:" + Build.PRODUCT
                + "\n版本显示:" + Build.DISPLAY
                + "\n系统定制商:" + Build.BRAND
                + "\n设备参数:" + Build.DEVICE
                + "\n开发代号:" + Build.VERSION.CODENAME
                + "\nSDK版本号:" + Build.VERSION.SDK_INT
                + "\nCPU类型:" + Build.CPU_ABI
                + "\n硬件类型:" + Build.HARDWARE
                + "\n主机:" + Build.HOST
                + "\n生产ID:" + Build.ID
                + "\nROM制造商:" + Build.MANUFACTURER;
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
