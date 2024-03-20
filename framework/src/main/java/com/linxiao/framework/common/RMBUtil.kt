package com.linxiao.framework.common

import android.text.TextUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 人民币金额计算工具
 *
 *
 *
 * 以BigDecimal作为运算对象封装人民币金额计算，避免使用浮点数而产生异常
 *
 * @author lx8421bcd
 * @since 2018-05-07
 */
object RMBUtil {

    /**
     * 输出金额字符串, 2位小数
     * @param fenAmount 金额 单位： 分
     * @return result string
     */
    fun getAmountDecimalString(fenAmount: Int): String {
        return getAmountString(divide(fenAmount, 100), "0.00")
    }

    /**
     * 获取金额整数，小数截断
     * @param fenAmount 金额 单位： 分
     * @return result string
     */
    fun getAmountIntegerString(fenAmount: Int): String {
        var format = ""
        val result = divide(fenAmount, 100).stripTrailingZeros()
        if (result.scale() > 0) {
            format = "0.00"
        }
        return getAmountString(result, format)
    }

    /**
     * 输出金额字符串，格式手动指定
     * @param fenAmount 金额对象
     * @param format 输出格式
     * @return formatted amount string
     */
    fun getAmountString(fenAmount: BigDecimal, format: String?): String {
        if (TextUtils.isEmpty(format)) {
            return if (fenAmount.toDouble() == 0.0) {
                "0"
            } else fenAmount.stripTrailingZeros().toPlainString()
        }
        val decimalFormat = DecimalFormat(format)
        return decimalFormat.format(fenAmount)
    }

    /**
     * 加
     * @param numbers 相加金额数组
     * @return result
     */
    fun add(vararg numbers: Number): BigDecimal {
        var sum = BigDecimal.ZERO
        for (num in numbers) {
            sum = sum.add(BigDecimal(num.toString()))
        }
        return sum
    }

    /**
     * 减
     * @param a 被减数
     * @param numbers 减数
     * @return result
     */
    fun subtract(a: Number, vararg numbers: Number): BigDecimal {
        var result = BigDecimal(a.toString())
        for (num in numbers) {
            result = result.subtract(BigDecimal(num.toString()))
        }
        return result
    }

    /**
     * 乘
     * @param numbers 乘数
     * @return result
     */
    fun multiply(vararg numbers: Number): BigDecimal {
        if (numbers.isEmpty()) {
            return BigDecimal.ZERO
        }
        var sum = BigDecimal.ONE
        for (num in numbers) {
            sum = sum.multiply(BigDecimal(num.toString()))
        }
        return sum
    }

    /**
     * 除
     * @param a 被除数
     * @param numbers 除数
     * @return result
     */
    fun divide(a: Number, vararg numbers: Number): BigDecimal {
        var result = BigDecimal(a.toString())
        for (num in numbers) {
            if (num.toDouble() == 0.0) {
                continue
            }
            result = result.divide(BigDecimal(num.toString()), 2, RoundingMode.HALF_DOWN)
        }
        return result
    }

}
