package com.linxiao.framework.common

import android.content.res.Resources
import kotlin.math.ceil

/**
 * 分辨率适配工具
 *
 * @author linxiao
 * @since 2016-11-03
 */
object DensityHelper {
    const val MDPI_IOS1X = 1f
    const val HDPI_IOS1d5X = 1.5f
    const val XHDPI_IOS2X = 2f
    const val XXHDPI_IOS3X = 3f

    /**
     * 是否动态调整分辨率
     */
    @JvmStatic
    var scaleDensityEnabled = false

    private var designedDensity = 0f
    private var originDensity = 0f
    private var originScaledDensity = 0f
    private var originDensityDpi = 0

    init {
        val dm = globalContext.resources.displayMetrics
        originDensity = dm.density
        originScaledDensity = dm.scaledDensity
        originDensityDpi = dm.densityDpi
    }

    @JvmStatic
    fun setDesignedDensity(designedDensity: Float) {
        this.designedDensity = designedDensity
    }

    /**
     * 使用此工具类时，重写Activity基类的getResources()方法，调用此方法以使全屏缩放生效
     * @param resources resources object from [Activity.getResources]
     */
    @JvmStatic
    fun onActivityGetResources(resources: Resources) {
        val dm = resources.displayMetrics
        if (originDensity == 0f) {
            originDensity = dm.density
            originScaledDensity = dm.scaledDensity
            originDensityDpi = dm.densityDpi
        }
        if (scaleDensityEnabled) {
            dm.density = designedDensity
            dm.scaledDensity = designedDensity
            dm.densityDpi = (designedDensity * 160).toInt()
        } else {
            dm.density = originDensity
            dm.scaledDensity = originScaledDensity
            dm.densityDpi = originDensityDpi
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = if (designedDensity > 0 && scaleDensityEnabled) designedDensity else originDensity
        return ceil(dpValue * scale).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        val scale = if (designedDensity > 0 && scaleDensityEnabled) designedDensity else originDensity
        return ceil(pxValue / scale).toInt()
    }

    @JvmStatic
    fun Number.toDp(): Int {
        return px2dp(this.toFloat())
    }

    @JvmStatic
    fun Number.toPx(): Int {
        return dp2px(this.toFloat())
    }
}