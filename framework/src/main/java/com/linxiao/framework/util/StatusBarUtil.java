package com.linxiao.framework.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Android 状态栏工具类
 * <p>主要针对不同的ROM尽可能的进行状态栏适配</p>
 * Created by linxiao on 2017/7/18.
 */
public final class StatusBarUtil {
    public static final String TAG = StatusBarUtil.class.getSimpleName();

    /**
     * API 23 以上无法由于修改底层实现而无法开启浅色状态栏的ROM黑名单
     * */
    private static final List<String> LIGHT_MODE_BLOCK_LIST = Arrays.asList("A Part Of Build.DISPLAY", "ZUI_2.3");

    /**
     * 修改状态栏为全透明
     * @param activity 需设置样式的Activity
     */
    public static void transparencyBar(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            
        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏颜色，5.0以上生效
     * @param activity 需设置样式的activity
     * @param  color 颜色值
     * */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
    
    /**
     * 设置状态栏深浅样式
     * <p>
     *     浅色样式即文字为深色，背景为浅色，仅针对API 19以上部分机型，以及API 23以上机型有效<br>
     *     对于API 23以上机型，强烈建议编写 style.xml 单独配置适配方案，一行代码解决问题
     * </p>
     * @param activity 需设置样式的activity
     * @param isLight 是否为浅色样式，true - 浅色，false - 深色
     * */
    public static boolean setStatusBarLightMode(Activity activity, boolean isLight) {
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        if (setStatusBarLightModeFlyme(activity.getWindow(), isLight)) {
            return true;
        }
        if (setStatusBarLightModeMIUI(activity.getWindow(), isLight)) {
            return true;
        }
        // 这里可以添加其他适配，由于某些第三方UI使用原生方法不管用，因此建议第三方UI的适配放在原生前
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        // 检查机型是否在无法显示浅色模式的黑名单中
        for (String blockROMName : LIGHT_MODE_BLOCK_LIST) {
            if (Build.DISPLAY.contains(blockROMName)) {
                return false;
            }
        }
        int cachedParams = activity.getWindow().getDecorView().getSystemUiVisibility();
        if (isLight) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    cachedParams | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        else {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    cachedParams & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        return true;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param isLightMode 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    private static boolean setStatusBarLightModeFlyme(Window window, boolean isLightMode) {
        if (window == null) {
            return false;
        }
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (isLightMode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            result = true;
        } catch (Exception ignored) {}
        return result;
    }
    
    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param isLightMode 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    @SuppressWarnings("unchecked")
    private static boolean setStatusBarLightModeMIUI(Window window, boolean isLightMode) {
        if (window == null) {
            return false;
        }
        boolean result = false;
        Class clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field  field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if(isLightMode){
                extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
            }else{
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int cachedParams = window.getDecorView().getSystemUiVisibility();
                if (isLightMode) {
                    window.getDecorView().setSystemUiVisibility(
                            cachedParams | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                else {
                    window.getDecorView().setSystemUiVisibility(
                            cachedParams & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
            result=true;
        }catch (Exception ignored){}
        return result;
    }
    
}
