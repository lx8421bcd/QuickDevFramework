package com.linxiao.framework.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * com.linxiao.framework.common.NumberUtil
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2020-06-08
 */
public class NumberUtilTest {

    @Test
    public void optInt() {
        System.out.println("-----optInt test-----");
        System.out.println(NumberUtil.optInt(null, 0));
        System.out.println(NumberUtil.optInt("", 1));
        System.out.println(NumberUtil.optInt("null", 2));
        System.out.println(NumberUtil.optInt("123", 3));
        System.out.println(NumberUtil.optInt("123.6", 4));
        System.out.println(NumberUtil.optInt("123.6.6.6", 5));
    }

    @Test
    public void optLong() {
        System.out.println("-----optLong test-----");
        System.out.println(NumberUtil.optLong(null, 0));
        System.out.println(NumberUtil.optLong("", 1));
        System.out.println(NumberUtil.optLong("null", 2));
        System.out.println(NumberUtil.optLong("123", 3));
        System.out.println(NumberUtil.optLong("123.6", 4));
        System.out.println(NumberUtil.optLong("123.6.6.6", 5));
    }

    @Test
    public void optFloat() {
        System.out.println("-----optFloat test-----");
        System.out.println(NumberUtil.optFloat(null, 0));
        System.out.println(NumberUtil.optFloat("", 1));
        System.out.println(NumberUtil.optFloat("null", 2));
        System.out.println(NumberUtil.optFloat("123", 3));
        System.out.println(NumberUtil.optFloat("123.6", 4));
        System.out.println(NumberUtil.optFloat("123.6.6.6", 5));
    }

    @Test
    public void optDouble() {
        System.out.println("-----optDouble test-----");
        System.out.println(NumberUtil.optDouble(null, 0));
        System.out.println(NumberUtil.optDouble("", 1));
        System.out.println(NumberUtil.optDouble("null", 2));
        System.out.println(NumberUtil.optDouble("123", 3));
        System.out.println(NumberUtil.optDouble("123.6", 4));
        System.out.println(NumberUtil.optDouble("123.6.6.6", 5));
    }

    @Test
    public void getWanUnitString() {
        System.out.println("-----getWanUnitString test-----");
        System.out.println(NumberUtil.getWanUnitString(1000, "w"));
        System.out.println(NumberUtil.getWanUnitString(1234567, "w"));
        System.out.println(NumberUtil.getWanUnitString(-1234567, "w"));
        System.out.println(NumberUtil.getWanUnitString(1234567.8, "万"));
        System.out.println(NumberUtil.getWanUnitString(1234567, null));

    }

    @Test
    public void getThousandSeparatorFormat() {
        System.out.println("-----getThousandSeparatorFormat test-----");
        System.out.println(NumberUtil.getThousandSeparatorFormat(100));
        System.out.println(NumberUtil.getThousandSeparatorFormat(1000));
        System.out.println(NumberUtil.getThousandSeparatorFormat(100000000000000.0));
        System.out.println(NumberUtil.getThousandSeparatorFormat(-100000000000000.0));
    }
}