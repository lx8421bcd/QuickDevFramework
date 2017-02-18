package com.linxiao.framework.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.linxiao.framework.support.log.Logger;

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
}
