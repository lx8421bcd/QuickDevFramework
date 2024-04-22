package com.linxiao.framework.architecture

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.linxiao.framework.FrameworkConfigs
import com.linxiao.framework.common.DensityHelper.onActivityGetResources
import com.linxiao.framework.language.AppLanguageHelper

/**
 * Base Application class in framework
 *
 * @author lx8421bcd
 * @since 2018-06-05
 */
abstract class BaseApplication : MultiDexApplication(), LifecycleEventObserver {

    companion object {
        /**
         * 检查应用是否处于前台
         * <p>
         * 使用LifeCycleEventObserver监测实现，更为安全和精确
         * </p>
         *
         * @return is app foreground
         */
        @JvmStatic
        var isAppForeground: Boolean = false
            private set
    }

    protected val TAG = this.javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        FrameworkConfigs.init()
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        onActivityGetResources(res)
        AppLanguageHelper.doOnContextGetResources(res)
        return res
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "Application Lifecycle onStateChanged: ${event.name}")
        isAppForeground = source.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

}