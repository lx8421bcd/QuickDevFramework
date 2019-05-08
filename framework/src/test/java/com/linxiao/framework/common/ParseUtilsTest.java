package com.linxiao.framework.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * unit test for {@link ParseUtils}
 * <p>
 * details for class usage and attention
 * </p>
 *
 * @author linxiao
 * @since 2019-05-08
 */
public class ParseUtilsTest {

    @Test
    public void test() {
        System.out.println("---------- ParseUtilsTest start ----------");
        optInt();
        optLong();
        optFloat();
        optDouble();
        System.out.println("---------- ParseUtilsTest end ----------");
    }

    @Test
    public void optInt() {
        System.out.println(ParseUtils.optInt("12", 0));
        System.out.println(ParseUtils.optInt("+12", 0));
        System.out.println(ParseUtils.optInt("-12", 0));
        System.out.println(ParseUtils.optInt("12.3", 0));
        System.out.println(ParseUtils.optInt("abc", -1));
        System.out.println(ParseUtils.optInt(null, -1));
    }

    @Test
    public void optLong() {
        System.out.println(ParseUtils.optLong("101251787", 0));
        System.out.println(ParseUtils.optLong("+101251787", 0));
        System.out.println(ParseUtils.optLong("-101251787", 0));
        System.out.println(ParseUtils.optLong("101251787.3", 0));
        System.out.println(ParseUtils.optLong("abc", -1));
        System.out.println(ParseUtils.optLong(null, -1));
    }

    @Test
    public void optFloat() {
        System.out.println(ParseUtils.optFloat("12", 0));
        System.out.println(ParseUtils.optFloat("+12", 0));
        System.out.println(ParseUtils.optFloat("-12", 0));
        System.out.println(ParseUtils.optFloat("12.3", 0));
        System.out.println(ParseUtils.optFloat("abc", -1));
        System.out.println(ParseUtils.optFloat(null, -1));
    }

    @Test
    public void optDouble() {
        System.out.println(ParseUtils.optDouble("12", 0));
        System.out.println(ParseUtils.optDouble("+12", 0));
        System.out.println(ParseUtils.optDouble("-12", 0));
        System.out.println(ParseUtils.optDouble("12.3", 0));
        System.out.println(ParseUtils.optDouble("abc", -1));
        System.out.println(ParseUtils.optDouble(null, -1));
    }
}