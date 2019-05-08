package com.linxiao.framework.common;

/**
 * common parse function collections
 * <p>
 * wrap some common parse functions, mainly string to base data types, handled frequently
 * thrown exceptions like NumberFormatException to avoid write try-catch everywhere
 * </p>
 *
 * @author linxiao
 * @since 2019-05-08
 */
public final class ParseUtils {

    /**
     * opt style parse function to parse string to int,
     * handle exceptions throws during parsing
     * @param value string that need to parse
     * @param fallback default value while exceptions were thrown during parsing
     * @return parsed or fallback value
     */
    public static int optInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * opt style parse function to parse string to long,
     * handle exceptions throws during parsing
     * @param value string that need to parse
     * @param fallback default value while exceptions were thrown during parsing
     * @return parsed or fallback value
     */
    public static long optLong(String value, long fallback) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * opt style parse function to parse string to float,
     * handle exceptions throws during parsing
     * @param value string that need to parse
     * @param fallback default value while exceptions were thrown during parsing
     * @return parsed or fallback value
     */
    public static float optFloat(String value, float fallback) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * opt style parse function to parse string to double,
     * handle exceptions throws during parsing
     * @param value string that need to parse
     * @param fallback default value while exceptions were thrown during parsing
     * @return parsed or fallback value
     */
    public static double optDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return fallback;
        }
    }
}
