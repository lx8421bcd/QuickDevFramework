package com.linxiao.framework.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat


/**
 * 获取屏幕宽, 单位 px
 *
 * @return screenWidth;
 */
fun getRealScreenWidth(): Int {
    val windowManager = ContextProvider.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val dm = DisplayMetrics()
    try {
        val method = Class.forName("android.view.Display")
            .getMethod("getRealMetrics", DisplayMetrics::class.java)
        method.invoke(display, dm)
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    }
    return dm.widthPixels
}

/**
 * 获取屏幕高(包含虚拟键盘)， 单位 px
 *
 * @return screenHeight;
 */
fun getRealScreenHeight(): Int {
    val windowManager = ContextProvider.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val dm = DisplayMetrics()
    try {
        val method = Class.forName("android.view.Display")
            .getMethod("getRealMetrics", DisplayMetrics::class.java)
        method.invoke(display, dm)
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    }
    return dm.heightPixels
}

/**
 * 获取没有虚拟按键(NavigationBar)的屏幕宽度
 *
 * 在这里必须传入Activity以准确计算当前Activity下的可用宽度
 *
 * @return screenHeight without virtual key height
 */
fun getUsableScreenWidth(context: Context?): Int {
    if (context == null) {
        return 0
    }
    val metrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

/**
 * 获取没有虚拟键盘的屏幕高度
 *
 * 由于有些虚拟键盘可以滑动隐藏（比如小米），
 * 因此在这里必须传入Activity以准确计算当前Activity下的可用高度
 *
 * @return screenHeight without virtual key height
 */
fun getUsableScreenHeight(context: Context?): Int {
    if (context == null) {
        return 0
    }
    val metrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

/**
 * 获取虚拟按键高度
 *
 * 由于有些虚拟键盘可以滑动隐藏（比如小米），
 * 因此这里需要动态获取Window并拿到实时高度
 *
 * @return virtual key height
 */
fun getVirtualKeyHeight(context: Context?): Int {
    return getRealScreenHeight() - getUsableScreenHeight(context)
}

/**
 * 获取status bar高度
 *
 * @return status bar height in px value
 */
@SuppressLint("PrivateApi", "DiscouragedApi", "InternalInsetResource")
fun getStatusBarHeight(): Int {
    var statusBarHeight = 0
    val resId = ContextProvider.get().resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        statusBarHeight = ContextProvider.get().resources.getDimensionPixelSize(resId)
    }
    return statusBarHeight
}

/**
 * 检查设备是否有底部导航栏
 */
@SuppressLint("PrivateApi", "DiscouragedApi")
fun hasNavigationBar(): Boolean {
    var hasNavigationBar = false
    val rs = ContextProvider.get().resources
    val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id)
    }
    try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val m = systemPropertiesClass.getMethod("get", String::class.java)
        val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
        if ("1" == navBarOverride) {
            hasNavigationBar = false
        } else if ("0" == navBarOverride) {
            hasNavigationBar = true
        }
    } catch (ignored: Exception) {
    }
    return hasNavigationBar
}

/**
 * 获取底部 navigation bar高度
 * @return status bar height in px value
 */
@SuppressLint("PrivateApi", "DiscouragedApi", "InternalInsetResource")
fun getNavigationBarHeight(): Int {
    if (!hasNavigationBar()) {
        return 0
    }
    val statusBarHeight = 0
    val resources = ContextProvider.get().resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else statusBarHeight
}

/**
 * 判断是否为夜间模式
 */
fun isDarkMode(): Boolean {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
        return true
    }
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
        return false
    }
    val mode = ContextProvider.get().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}

/**
 * 修改状态栏为全透明
 * @param activity 需设置样式的Activity
 */
fun transparencyStatusBar(activity: Activity) {
    val window = activity.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
}

/**
 * 设置状态栏颜色，5.0以上生效
 * @param activity 需设置样式的activity
 * @param  color 颜色值
 */
fun setStatusBarColor(activity: Activity, color: Int) {
    val window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

/**
 * 设置状态栏深浅样式
 *
 *
 * 浅色样式即文字为深色，背景为浅色，API 23以上机型有效<br></br>
 * 对于API 23以上机型，强烈建议编写 style.xml 单独配置适配方案，一行代码解决问题
 *
 * @param activity 需设置样式的activity
 * @param isLight 是否为浅色样式，true - 浅色，false - 深色
 */
fun setStatusBarLightMode(activity: Activity, isLight: Boolean) {
    WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
        this.isAppearanceLightStatusBars = isLight
    }
}

/**
 * 是否允许截屏
 * <p>关闭之后调用系统截屏为黑屏</p>
 *
 * @param enabled 是否允许
 */
fun allowScreenshots(activity: Activity, enabled: Boolean) {
    if (enabled) {
        activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    else {
        activity.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
