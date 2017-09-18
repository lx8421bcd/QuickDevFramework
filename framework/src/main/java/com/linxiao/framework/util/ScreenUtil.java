package com.linxiao.framework.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.linxiao.framework.QDFApplication;

import java.lang.reflect.InvocationTargetException;
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
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = QDFApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = QDFApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽, 单位 px
     * @return screenWidth;
     * */
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) QDFApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return dm.widthPixels;
    }
    
    /**
     * 获取屏幕高(包含虚拟键盘)， 单位 px
     * @return screenHeight;
     * */
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) QDFApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return dm.heightPixels;
    }
    
    /**
     * 获取没有虚拟键盘的屏幕高度
     * @return screenHeight without virtual key height
     * */
    public static int getScreenHeightNoVirtualKey() {
        Resources resources = QDFApplication.getAppContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
    
    /**
     * 获取虚拟按键高度
     * @return virtual key height
     * */
    public static int getVirtualKeyHeight() {
        return getScreenHeight() - getScreenHeightNoVirtualKey();
    }
    
    /**
     * 获取status bar高度
     * @return status bar height in px value
     * */
    public static int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(obj).toString());
            statusBarHeight = QDFApplication.getAppContext().getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
    
}
