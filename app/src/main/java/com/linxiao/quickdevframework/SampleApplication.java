package com.linxiao.quickdevframework;

import com.linxiao.framework.BaseApplication;
import com.squareup.leakcanary.LeakCanary;

/**
 *
 * Created by LinXiao on 2016-11-27.
 */
public class SampleApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }


}
