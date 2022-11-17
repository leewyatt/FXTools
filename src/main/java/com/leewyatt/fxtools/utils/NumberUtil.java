package com.leewyatt.fxtools.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author LeeWyatt
 */
public class NumberUtil {
    /**
     * 保留指定位数的小数
     */
    public static double roundingDouble(String strDouble, int newScale) {
        return new BigDecimal(strDouble).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 保留指定位数的小数
     */
    public static double roundingDouble(double value, int newScale) {
        return new BigDecimal(value).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算用于显示的缩放比率
     */
    public static double computeScaleRate(double w, double h,int max) {
        double rate;
        if (w >= h && w > max) {
            rate = max / w;
        } else if (h > w && h > max) {
            rate = max / h;
        } else {
            rate = 1.0;
        }
        return rate;
    }
}
