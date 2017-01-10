package com.linxiao.framework.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.linxiao.framework.support.log.LogManager;

import java.util.List;

/**
 * 系统相关工具类
 * Created by linxiao on 2016/12/12.
 */
public class ApplicationUtil {

    private static final String TAG = ApplicationUtil.class.getSimpleName();

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
                LogManager.d(TAG, String.format("the %s is running", packageName));
                return true;
            }
        }
        LogManager.d(TAG, String.format("the %s is not running", packageName));
        return false;
    }
}
