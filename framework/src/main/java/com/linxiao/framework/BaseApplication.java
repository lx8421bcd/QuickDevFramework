package com.linxiao.framework;

import android.app.Application;
import android.content.Context;

import com.linxiao.framework.event.ExitAppEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 应用基类，主要用于提供Application的基础功能，以及为Framework层提供Application Context
 * Created by LinXiao on 2016-11-24.
 */

public abstract class BaseApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static void exitApplication() {
        EventBus.getDefault().post(new ExitAppEvent());
    }

    public static Context getAppContext() {
        return appContext;
    }
}
