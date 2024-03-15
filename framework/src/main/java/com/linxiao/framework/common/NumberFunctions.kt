package com.linxiao.framework.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <p>
 * 数字处理工具合集
 * </p>
 *
 * @author linxiao
 * @since 2020-06-08
 */
public final class NumberUtil {

    /**
     * safe parse string to number type method
     * @param numStr number string
     * @param def return value when parse failed
     * @return parsed number or default value
     */
    public static int optInt(String numStr, int def) {
        if (numStr == null || numStr.isEmpty()) {
            return def;
        }
        try {
            return (int) Double.parseDouble(numStr);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * safe parse string to number type method
     * @param numStr number string
     * @param def return value when parse failed
     * @return parsed number or default value
     */
    public static long optLong(String numStr, long def) {
        if (numStr == null || numStr.isEmpty()) {
            return def;
        }
        try {
            return (long) Double.parseDouble(numStr);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * safe parse string to number type method
     * @param numStr number string
     * @param def return value when parse failed
     * @return parsed number or default value
     */
    public static float optFloat(String numStr, float def) {
        if (numStr == null || numStr.isEmpty()) {
            return def;
        }
        try {
            return Float.parseFloat(numStr);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * safe parse string to number type method
     * @param numStr number string
     * @param def return value when parse failed
     * @return parsed number or default value
     */
    public static double optDouble(String numStr, double def) {
        if (numStr == null || numStr.isEmpty()) {
            return def;
        }
        try {
            return Double.parseDouble(numStr);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * get number string using wan unit(ten thousand)
     * <p>
     * if input num < 10000, return num
     * if input num > 10000, return num / 10000 + unit（万/W）
     * </p>
     * @param num input number
     * @param unit unit
     * @return num string
     */
    public static String getWanUnitString(double num , String unit) {
        String uc;
        if (unit == null || unit.isEmpty()) {
            unit = "w";
        }
        if (Math.abs(num) < 10000) {
            uc = String.valueOf(num);
        } else {
            float value = (float) (num / 10000.0);
            if (value >= 100.0 || num % 10000 == 0) {
                uc = Math.round((float) (num / 10000.0)) + unit;
            } else {
                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
                uc = bd + "";
                if (uc.endsWith(".0")) {
                    uc = Math.round(value) + unit;
                } else {
                    uc = uc + unit;
                }
            }
        }
        return uc;
    }

    /**
     * get thousand separator formatted style number string
     * @param num input number
     * @return formatted string
     */
    public static String getThousandSeparatorFormat(double num) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        return decimalFormat.format(num);
    }

}
