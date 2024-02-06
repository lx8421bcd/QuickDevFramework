package com.linxiao.framework.architecture

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 启动Activity基类
 *
 * 执行App启动的预处理，此处用于执行框架模块的预处理操作。
 *
 * Created by linxiao on 2016/12/5.
 */
@SuppressLint("CustomSplashScreen")
abstract class BaseSplashActivity : BaseActivity() {

    protected val splashScreen by lazy {
        return@lazy installSplashScreen()
    }

    /**
     * 控制系统启动屏显示变量
     */
    protected val keepSplashScreen = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 保持Android系统级启动屏直至Splash页面内的初始化内容初始化完成，避免闪屏
        splashScreen.setKeepOnScreenCondition { keepSplashScreen.get() }
        super.onCreate(savedInstanceState)
    }

}
