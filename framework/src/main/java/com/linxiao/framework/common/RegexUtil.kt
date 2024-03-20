package com.linxiao.framework.common

import android.text.TextUtils
import java.util.Locale
import java.util.regex.Pattern

/**
 * 正则表达式工具类
 *
 * @author lx8421bcd
 * @since 2015-11-26
 */
object RegexUtil {
    private val coefficients = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    private val remainderResults = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    private val correspondCodes = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')

    /**
     * 检查是否为正确的手机号
     */
    @JvmStatic
    fun isValidPhoneNum(phoneNumber: String?): Boolean {
        return phoneNumber != null && phoneNumber.matches("[1][3578]\\d{9}".toRegex())
    }

    /**
     * 检查是否为有效QQ号
     * @param qq qq号
     */
    fun isValidQQNum(qq: String?): Boolean {
        return qq != null && qq.matches("^[1-9][0-9]{4,12}$".toRegex())
    }

    /**
     * 检查是否为正确邮箱
     */
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && email.matches("^([a-z0-9A-Z]+[-_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$".toRegex())
    }

    /**
     * 检查第二代身份证号合法性
     */
    fun isValidIDCard(strIdNumber: String?): Boolean {
        if (strIdNumber == null) {
            return false
        }
        if (!strIdNumber.matches("\\d{17}[0-9xX]".toRegex())) {
            return false
        }
        /* 验证身份证号合法性，前17位乘以以下系数的结果与11求余，得到的结果应为remainderResults里面11个数字中的一个，
            分别对应到correspondCode中的身份证号第十八位的字符 */
        // 用于验证身份证号合法性的相乘系数
        var calculatedEighteenCode = 0.toChar()
        var sum = 0
        for (i in 0 until strIdNumber.length - 1) {
            val number = strIdNumber[i].code - '0'.code
            sum += number * coefficients[i]
        }
        val result = sum % 11
        for (i in remainderResults.indices) {
            if (result == remainderResults[i]) {
                calculatedEighteenCode = correspondCodes[i]
                break
            }
        }
        return calculatedEighteenCode == strIdNumber.uppercase(Locale.getDefault())[17]
    }

    /**
     * 从身份证号中读取出生日期
     * @param idString 身份证号
     * @return String yyyy-mm-dd 正确输出; null 身份证号非法或其它异常
     */
    fun getBirthdayFromIdNumber(idString: String): String? {
        if (!isValidIDCard(idString)) {
            return null
        }
        val year: String
        val month: String
        val day: String
        val birthdayPattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*")
        val matcher = birthdayPattern.matcher(idString)
        if (matcher.find()) {
            year = matcher.group(1) ?: ""
            month = matcher.group(2) ?: ""
            day = matcher.group(3) ?: ""
            return "$year-$month-$day"
        }
        return null
    }

    /**
     * 从身份证号中读取性别
     * @param strIdNumber 身份证号
     * @return 0: 非法输入, 1: 男, 2:女
     */
    fun getSexFromIdNumber(strIdNumber: String): Int {
        if (!isValidIDCard(strIdNumber)) {
            return 0
        }
        val sexCode = strIdNumber[16].code - '0'.code
        return if (sexCode % 2 != 0) {
            1
        } else {
            2
        }
    }
}
