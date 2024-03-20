package com.linxiao.framework.common;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 人民币金额计算工具
 *
 * <p>
 * 以BigDecimal作为运算对象封装人民币金额计算，避免使用浮点数而产生异常
 * </p>
 * @author lx8421bcd
 * @since 2018-05-07
 */
public class RMBUtil {

    public enum AmountUnit {
        YUAN, JIAO, FEN
    }
    
    /**
     * 获取金额运算对象
     * @param amount 金额 单位： 分
     * @return instance of AmountOperator
     */
    public static AmountOperator getOperator(int amount) {
        return new AmountOperator(amount);
    }
    
    /**
     * 获取金额运算对象
     * @param amount 金额象字符串
     * @param unit 金额单位
     * @return instance of AmountOperator
     */
    public static AmountOperator getOperator(String amount, AmountUnit unit) {
        BigDecimal amountDecimal = new BigDecimal(amount);
        return new AmountOperator(amountDecimal, unit);
    }
    
    /**
     * 输出金额字符串, 2位小数
     * @param amount 金额 单位： 分
     * @return result string
     */
    public static String getAmountDecimalString(int amount) {
        return getAmountString(divide(amount, 100), "0.00");
    }
    
    /**
     * 获取金额整数，小数截断
     * @param amount 金额 单位： 分
     * @return result string
     */
    public static String getAmountIntegerString(int amount) {
        String format = "";
        BigDecimal result = divide(amount, 100).stripTrailingZeros();
        if (result.scale() > 0) {
            format = "0.00";
        }
        return getAmountString(result, format);
    }
    
    /**
     * 输出金额字符串，格式手动指定
     * @param amount 金额对象
     * @param format 输出格式
     * @return formatted amount string
     */
    public static String getAmountString(BigDecimal amount, String format) {
        if (TextUtils.isEmpty(format)) {
            if (amount.doubleValue() == 0) {
                return "0";
            }
            return amount.stripTrailingZeros().toPlainString();
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(amount);
    }
    
    /**
     * 加
     * @param numbers 相加金额数组
     * @return result
     */
    public static BigDecimal add(@NonNull Number... numbers) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Number num : numbers) {
            sum = sum.add(new BigDecimal(String.valueOf(num)));
        }
        return sum;
    }
    
    /**
     * 减
     * @param a 被减数
     * @param numbers 减数
     * @return result
     */
    public static BigDecimal subtract(@NonNull Number a, @NonNull Number... numbers) {
        BigDecimal result = new BigDecimal(String.valueOf(a));
        for (Number num : numbers) {
            result = result.subtract(new BigDecimal(String.valueOf(num)));
        }
        return result;
    }
    
    /**
     * 乘
     * @param numbers 乘数
     * @return result
     */
    public static BigDecimal multiply(@NonNull Number... numbers) {
        if (numbers.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ONE;
        for (Number num : numbers) {
            sum = sum.multiply(new BigDecimal(String.valueOf(num)));
        }
        return sum;
    }
    
    /**
     * 除
     * @param a 被除数
     * @param numbers 除数
     * @return result
     */
    public static BigDecimal divide(@NonNull Number a, @NonNull Number... numbers) {
        BigDecimal result = new BigDecimal(String.valueOf(a));
        for (Number num : numbers) {
            if (num.doubleValue() == 0) {
                continue;
            }
            result = result.divide(new BigDecimal(String.valueOf(num)), 2, RoundingMode.HALF_DOWN);
        }
        return result;
    }
    
    /**
     * 金额计算操作包装类
     * <p>内部运算和输出均以"元"为单位</p>
     */
    public static class AmountOperator {
        
        private BigDecimal amount;
    
        /**
         * 输入int值构造时默认为分
         * @param amount 金额 单位：分
         */
        AmountOperator(int amount) {
            this.amount = new BigDecimal(amount).divide(BigDecimal.valueOf(100.0),2, RoundingMode.HALF_DOWN);
        }
    
        /**
         * 传入BigDecimal对象和货币单位构造
         * @param decimal 金额对象
         * @param unit 货币单位：元 角 分
         */
        AmountOperator(BigDecimal decimal, AmountUnit unit) {
            switch (unit) {
                case YUAN -> amount = decimal;
                case JIAO -> amount = decimal.divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_DOWN);
                case FEN -> amount = decimal.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
            }
        }
        
        public AmountOperator add(Number number) {
            amount = amount.add(new BigDecimal(String.valueOf(number)));
            return this;
        }
        
        public AmountOperator subtract(Number number) {
            amount = amount.subtract(new BigDecimal(String.valueOf(number)));
            return this;
        }
        
        public AmountOperator multiply(Number number) {
            amount = amount.multiply(new BigDecimal(String.valueOf(number)));
            return this;
        }
        
        public AmountOperator divide(Number number) {
            amount = amount.divide(new BigDecimal(String.valueOf(number)),  RoundingMode.HALF_DOWN);
            return this;
        }
        public AmountOperator divide(Number number,int scale) {
            amount = amount.divide(new BigDecimal(String.valueOf(number)), scale, RoundingMode.HALF_DOWN);
            return this;
        }
        public int intValue() {
            return amount.intValue();
        }
        
        public double doubleValue() {
            return amount.setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        }

        public int toFenValue() {
            return multiply(100).intValue();
        }
        
        public String stringValue() {
            return new DecimalFormat("0.00").format(amount);
        }
    
        @Override
        public String toString() {
            return stringValue();
        }
    }
}
