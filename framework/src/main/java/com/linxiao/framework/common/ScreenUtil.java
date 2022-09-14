package com.linxiao.framework.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import java.lang.reflect.Method;

/**
 * dp px 转换工具
 *
 * @author linxiao
 * @version 1.0
 */
public class ScreenUtil {

    private ScreenUtil() {}

    /**
     * 获取屏幕宽, 单位 px
     *
     * @return screenWidth;
     */
    public static int getRealScreenWidth() {
        WindowManager windowManager = (WindowManager) ContextProvider.get().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Method method = Class.forName("android.view.Display").getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高(包含虚拟键盘)， 单位 px
     *
     * @return screenHeight;
     */
    public static int getRealScreenHeight() {
        WindowManager windowManager = (WindowManager) ContextProvider.get().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Method method = Class.forName("android.view.Display").getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return dm.heightPixels;
    }

    /**
     * 获取没有虚拟键盘的屏幕宽度
     * <p>在这里必须传入Activity以准确计算当前Activity下的可用宽度</p>
     *
     * @return screenHeight without virtual key height
     */
    public static int getUsableScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }

    /**
     * 获取没有虚拟键盘的屏幕高度
     * <p>由于有些虚拟键盘可以滑动隐藏（比如小米），
     * 因此在这里必须传入Activity以准确计算当前Activity下的可用高度</p>
     *
     * @return screenHeight without virtual key height
     */
    public static int getUsableScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics.heightPixels;
    }

    /**
     * 获取虚拟按键高度
     * <p>由于有些虚拟键盘可以滑动隐藏（比如小米），
     * 因此这里需要动态获取Window并拿到实时高度</p>
     *
     * @return virtual key height
     */
    public static int getVirtualKeyHeight(Context context) {
        return getRealScreenHeight() - getUsableScreenHeight(context);
    }

    /**
     * 获取status bar高度
     *
     * @return status bar height in px value
     */
    @SuppressLint("PrivateApi")
    public static int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resId = ContextProvider.get().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = ContextProvider.get().getResources().getDimensionPixelSize(resId);
        }
        return statusBarHeight;
    }

    /**
     * 检查设备是否有底部导航栏
     */
    @SuppressLint("PrivateApi")
    public static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = ContextProvider.get().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception ignored) {}
        return hasNavigationBar;
    }

    /**
     * 获取底部 navigation bar高度
     * @return status bar height in px value
     * */
    @SuppressLint("PrivateApi")
    public static int getNavigationBarHeight() {
        if (!hasNavigationBar()) {
            return 0;
        }
        int statusBarHeight = 0;
        Resources resources = ContextProvider.get().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 判断是否为夜间模式
     */
    public static boolean isDarkMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            return true;
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            return false;
        }
        int mode = ContextProvider.get().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }


}
