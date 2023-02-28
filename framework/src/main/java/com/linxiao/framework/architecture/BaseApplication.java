package com.linxiao.framework.architecture;

import android.content.res.Resources;

import androidx.multidex.MultiDexApplication;

import com.linxiao.framework.language.AppLanguageHelper;
import com.linxiao.framework.common.DensityHelper;

/**
 * Base Application class in framework
 *
 */
public abstract class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        DensityHelper.onActivityGetResources(res);
        AppLanguageHelper.doOnContextGetResources(res);
        return res;
    }
}
