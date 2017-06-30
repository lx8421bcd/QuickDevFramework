package com.linxiao.framework.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.linxiao.framework.BaseApplication;

/**
 * dp px 转换工具
 *
 * @author linxiao
 * @version 1.0
 */
public class DensityUtil {

    private DensityUtil() {}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = BaseApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = BaseApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽, 单位 px
     * @return screenWidth;
     * */
    public static int getScreenWidth() {
        Resources resources = BaseApplication.getAppContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
    
    /**
     * 获取屏幕高， 单位 px
     * @return screenHeight;
     * */
    public static int getScreenHeight() {
        Resources resources = BaseApplication.getAppContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
