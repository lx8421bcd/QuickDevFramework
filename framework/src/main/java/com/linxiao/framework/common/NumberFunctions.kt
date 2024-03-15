package com.linxiao.framework.common

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.abs

/**
 *
 *
 * 数字处理工具合集
 *
 *
 * @author linxiao
 * @since 2020-06-08
 */

/**
 * safe parse string to number type method
 * @param def return value when parse failed
 * @return parsed number or default value
 */
fun String?.optInt(def: Int): Int {
    return if (this.isNullOrEmpty()) {
        def
    } else try {
        this.toDouble().toInt()
    } catch (e: Exception) {
        def
    }
}

/**
 * safe parse string to number type method
 * @param def return value when parse failed
 * @return parsed number or default value
 */
fun String?.optLong(def: Long): Long {
    return if (this.isNullOrEmpty()) {
        def
    } else try {
        this.toDouble().toLong()
    } catch (e: Exception) {
        def
    }
}

/**
 * safe parse string to number type method
 * @param def return value when parse failed
 * @return parsed number or default value
 */
fun String?.optFloat(def: Float): Float {
    return if (this.isNullOrEmpty()) {
        def
    } else try {
        this.toFloat()
    } catch (e: Exception) {
        def
    }
}

/**
 * safe parse string to number type method
 * @param def return value when parse failed
 * @return parsed number or default value
 */
fun String?.optDouble(def: Double): Double {
    return if (this.isNullOrEmpty()) {
        def
    } else try {
        this.toDouble()
    } catch (e: Exception) {
        def
    }
}

/**
 * get number string using wan unit(ten thousand)
 *
 *
 * if input num < 10000, return num
 * if input num > 10000, return num / 10000 + unit（万/W）
 *
 * @param unit unit
 * @return num string
 */
fun Number.toWanUnitString(unit: String = "w"): String {
    val num = this.toDouble()
    var uc: String
    if (abs(num) < 10000) {
        uc = num.toString()
    } else {
        val value = (num / 10000.0).toFloat()
        if (value >= 100.0 || num % 10000 == 0.0) {
            uc = Math.round((num / 10000.0).toFloat()).toString() + unit
        } else {
            var bd = BigDecimal(value.toDouble())
            bd = bd.setScale(1, BigDecimal.ROUND_DOWN)
            uc = bd.toString() + ""
            uc = if (uc.endsWith(".0")) {
                Math.round(value).toString() + unit
            } else {
                uc + unit
            }
        }
    }
    return uc
}

/**
 * get thousand separator formatted style number string
 * @param num input number
 * @return formatted string
 */
fun Number.thousandSeparatorFormat(): String {
    val decimalFormat = DecimalFormat(",###")
    return decimalFormat.format(this.toDouble())
}
