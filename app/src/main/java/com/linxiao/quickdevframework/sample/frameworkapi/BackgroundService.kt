package com.linxiao.quickdevframework.sample.frameworkapi;

import android.app.IntentService;
import android.content.Intent;

import com.linxiao.framework.dialog.DialogExtensionsKt;


/**
 * 用于测试全局ActivityDialog的使用
 * Created by linxiao on 2016-12-11.
 */
public class BackgroundService extends IntentService {


    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DialogExtensionsKt.showAlert("this is top dialog from service");
    }
}
