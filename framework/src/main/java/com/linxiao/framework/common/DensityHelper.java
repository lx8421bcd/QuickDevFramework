package com.linxiao.framework.common;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 分辨率适配工具
 *
 * @author linxiao
 * @since 2016-11-03
 */
public class DensityHelper {

    public static final float MDPI_IOS1X = 1f;
    public static final float HDPI_IOS1d5X = 1.5f;
    public static final float XHDPI_IOS2X = 2f;
    public static final float XXHDPI_IOS3X = 3f;
    /**
     * 是否动态调整分辨率
     */
    private static boolean scaleDensityEnabled;
    private static float designedDensity = 0f;

    private static float originDensity = 0f;
    private static float originScaledDensity = 0f;
    private static int originDensityDpi = 0;

    static {
        DisplayMetrics dm = ContextProvider.get().getResources().getDisplayMetrics();
        originDensity = dm.density;
        originScaledDensity = dm.scaledDensity;
        originDensityDpi = dm.densityDpi;
    }

    public static boolean isScaleDensityEnabled() {
        return scaleDensityEnabled;
    }

    public static void setScaleDensityEnabled(boolean enabled) {
        scaleDensityEnabled = enabled;
    }

    public static void setDesignedDensity(float designedDensity) {
        DensityHelper.designedDensity = designedDensity;
    }

    /**
     * 使用此工具类时，重写Activity基类的getResources()方法，调用此方法以使全屏缩放生效
     * @param resources resources object from {@link Activity#getResources()}
     */
    public static void onActivityGetResources(Resources resources) {
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (originDensity == 0f) {
            originDensity = dm.density;
            originScaledDensity = dm.scaledDensity;
            originDensityDpi = dm.densityDpi;
        }
        if (scaleDensityEnabled) {
            dm.density = designedDensity;
            dm.scaledDensity = designedDensity;
            dm.densityDpi = (int) (designedDensity * 160);
        }
        else {
            dm.density = originDensity;
            dm.scaledDensity = originScaledDensity;
            dm.densityDpi = originDensityDpi;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        float scale = designedDensity >= 0 ? designedDensity : originDensity;
        return (int) Math.ceil(dpValue * scale);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        float scale = designedDensity >= 0 ? designedDensity : originDensity;
        return (int) Math.ceil(pxValue / scale);
    }
}
