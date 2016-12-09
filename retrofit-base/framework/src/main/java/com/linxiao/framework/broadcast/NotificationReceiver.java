package com.linxiao.framework.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.activity.NotificationResumeActivity;
import com.linxiao.framework.support.notification.NotificationWrapper;

import java.util.List;

/**
 * 用于接收应用通知消息点击的事件并根据当前应用的状态做出处理
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle notificationExtra = intent.getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
        if (notificationExtra == null) {
            return;
        }
        if (isAppAlive(context, context.getPackageName())) {
            Intent destIntent = new Intent();
            String destKey = notificationExtra.getString(NotificationWrapper.KEY_DEST_ACTIVITY_NAME);
            if (TextUtils.isEmpty(destKey)) {
                destKey = NotificationResumeActivity.class.getName();
            }
            try {
                Class<?> destActivityClass = Class.forName(destKey);
                destIntent.setClass(context, destActivityClass);
                destIntent.putExtras(notificationExtra);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                destIntent.setClass(context, NotificationResumeActivity.class);
            }
            destIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(destIntent);
        }
        else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, notificationExtra);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        System.out.println(processInfo.toString());
        for(int i = 0; i < processInfo.size(); i++){
            if(processInfo.get(i).processName.equals(packageName)){
                Log.i(TAG, String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i(TAG, String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }


}
