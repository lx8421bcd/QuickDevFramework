package com.linxiao.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * <p></p>
 * <p>创建时间:2015-11-26</p>
 *
 * @author linxiao
 * @version 1.0
 */
public class RegexUtil {

    private static final int coefficients[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final int remainderResults[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final char correspondCodes[] = {'1','0','X','9','8','7','6','5','4','3','2'};

    private RegexUtil() {}

    /**
     * 检查是否为正确的手机号
     */
    public static boolean checkPhoneNumberLegality(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("[1][3578]\\d{9}");
    }

    /**
     * 检查是否为正确邮箱
     * */
    public static boolean checkEmailLegality(String email) {

        return email != null && email.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    }

    /**
     * 检查第二代身份证号合法性
     * */
    public static boolean checkIdNumberLegality(String strIdNumber) {
        if(strIdNumber == null) {
            return false;
        }
        if (!strIdNumber.matches("\\d{17}[0-9xX]")) {
            return false;
        }
        /*验证身份证号合法性，前17位乘以以下系数的结果与11求余，得到的结果应为remainderResults里面11个数字中的一个，
            分别对应到correspondCode中的身份证号第十八位的字符*/
        //用于验证身份证号合法性的相乘系数
        char calculatedEighteenCode = 0;

        int sum = 0;
        for (int i = 0; i < strIdNumber.length() - 1; i++) {
            int number = strIdNumber.charAt(i) - '0';
            sum += number * coefficients[i];
        }
        int result = sum % 11;
        for(int i = 0; i < remainderResults.length; i++) {
            if (result == remainderResults[i]) {
                calculatedEighteenCode = correspondCodes[i];
                break;
            }
        }
        return calculatedEighteenCode == strIdNumber.toUpperCase().charAt(17);
    }

    /**
     * 从身份证号中读取出生日期
     * @param strIdNumber 身份证号
     * @return String yyyy-mm-dd 正确输出; null 身份证号非法或其它异常
     * */
    public static String getBirthdayFromIdNumber(String strIdNumber) {
        if(!checkIdNumberLegality(strIdNumber)) {
            return null;
        }
        String year;
        String month;
        String day;
        Pattern birthdayPattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");
        Matcher matcher = birthdayPattern.matcher(strIdNumber);
        if(matcher.find()) {
            year = matcher.group(1);
            month = matcher.group(2);
            day = matcher.group(3);
            return year + "-" + month + "-" + day;
        }
        return null;
    }

    /**
     * 从身份证号中读取性别
     * @param strIdNumber 身份证号
     * @return 0: 非法输入, 1: 男, 2:女
     * */
    public static int getSexFromIdNumber(String strIdNumber) {
        if(!checkIdNumberLegality(strIdNumber)) {
            return 0;
        }
        int sexCode = strIdNumber.charAt(16) - '0';
        if (sexCode % 2 != 0) {
            return 1;
        }
        else {
            return 2;
        }
    }

}
