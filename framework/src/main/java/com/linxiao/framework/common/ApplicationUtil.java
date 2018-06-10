package com.linxiao.framework.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.linxiao.framework.log.Logger;

import java.io.File;
import java.util.List;

/**
 * 系统相关工具类
 * Created by linxiao on 2016/12/12.
 */
public class ApplicationUtil {

    private static final String TAG = ApplicationUtil.class.getSimpleName();

    private ApplicationUtil() {}

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
                Logger.d(TAG, String.format("the %s is running", packageName));
                return true;
            }
        }
        Logger.d(TAG, String.format("the %s is not running", packageName));
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
            Logger.e(TAG, "cannot found apk file, path = " + apkPath);
            return;
        }
        if (!apkPath.endsWith(".apk")) {
            Logger.e(TAG, "illegal file : " + apkPath);
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
    private static String getHandSetInfo() {
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
}
