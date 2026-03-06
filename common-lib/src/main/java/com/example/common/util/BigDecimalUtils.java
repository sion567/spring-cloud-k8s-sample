package com.example.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class BigDecimalUtils {

    private static final int DEFAULT_SCALE = 2;
    // 四舍五入 (HALF_UP)
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    /**
     * 安全创建 BigDecimal (禁止使用 new BigDecimal(double))
     */
    public static BigDecimal create(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof String) return new BigDecimal((String) value);
        return BigDecimal.valueOf(Double.parseDouble(value.toString()));
    }

    /**
     * 加法 (a + b)
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return Objects.requireNonNullElse(a, BigDecimal.ZERO)
                .add(Objects.requireNonNullElse(b, BigDecimal.ZERO))
                .setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 減法 (a - b)
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return Objects.requireNonNullElse(a, BigDecimal.ZERO)
                .subtract(Objects.requireNonNullElse(b, BigDecimal.ZERO))
                .setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 乘法 (a * b)
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return Objects.requireNonNullElse(a, BigDecimal.ZERO)
                .multiply(Objects.requireNonNullElse(b, BigDecimal.ZERO))
                .setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 除法 (a / b)，防範除不盡異常
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b == null || b.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("除数不能为空或者零");
        }
        return a.divide(b, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 比較大小 (a == b) -> 返回 true
     * 解決了 equals 比較精度 (1.0 vs 1.00) 的問題
     */
    public static boolean isEqual(BigDecimal a, BigDecimal b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }

    /**
     * 格式化金额：强制保留2位小数
     */
    public static String format(BigDecimal amount) {
        if (amount == null) return "0.00";
        return amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING).toString();
    }
}
