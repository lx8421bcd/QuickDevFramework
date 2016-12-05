package com.linxiao.framework.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.support.notification.NotificationWrapper;

import java.util.List;

/**
 *
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationTransferActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAppAlive(this, this.getPackageName())) {
            Bundle notificationExtra = getIntent().getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
            String destKey = notificationExtra.getString(NotificationWrapper.KEY_DEST_ACTIVITY_NAME);
            if (TextUtils.isEmpty(destKey)) {
                return;
            }
            try {
                Class<?> destActivityClass = Class.forName(destKey);
                Intent destIntent = new Intent(this, destActivityClass);
                destIntent.putExtras(notificationExtra);
                this.startActivity(destIntent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            Bundle notificationExtra = getIntent().getBundleExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA);
            Intent launchIntent = this.getPackageManager().getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationWrapper.KEY_NOTIFICATION_EXTRA, notificationExtra);
            this.startActivity(launchIntent);
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
        for(int i = 0; i < processInfo.size(); i++){
            if(processInfo.get(i).processName.equals(packageName)){
                Log.i("NotificationLaunch",
                        String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i("NotificationLaunch",
                String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }
}
