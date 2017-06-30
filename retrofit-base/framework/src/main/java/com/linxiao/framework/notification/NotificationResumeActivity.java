package com.linxiao.framework.notification;


import android.app.Activity;

/**
 * 用于在点击通知时将app从后台唤起到前台
 * Created by LinXiao on 2016-12-08.
 */
public class NotificationResumeActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}
