package com.leewyatt.fxtools.utils;

import com.leewyatt.fxtools.utils.convert.HSL;
import com.leewyatt.fxtools.utils.convert.RGB;
import com.leewyatt.fxtools.utils.convert.RgbHslConverter;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author LeeWyatt
 */
public class CustomColorUtil {
    /**
     * 整数形式
     */
    public static final int TYPE_INT = 0;
    /**
     * 小数形式
     */
    public static final int TYPE_DECIMAL = 1;
    public static String toHsl(Color color, boolean withAlpha, int type) {

        HSL hsl = RgbHslConverter.RGB2HSL(new RGB(
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255)));
        int hue = Math.round(hsl.getH());
        double saturation = hsl.getS() / 255.0;
        double lightness = hsl.getL() / 255.0;
        String s;
        String l;
        if (type == TYPE_INT || type == TYPE_DECIMAL) {
            s = roundNoZero(saturation, 2);
            l = roundNoZero(lightness, 2);
        } else {
            s = convertToPerInt(saturation);
            l = convertToPerInt(lightness);
        }
        String opacity = roundNoZero(color.getOpacity(), 2);
        opacity = "1".equals(opacity) ? "1.0" : opacity;
        String result = hue + "," + s + "," + l;
        return withAlpha ? result + "," + opacity : result;
    }


    public static String toHsb(Color fxColor, boolean withAlpha, int type) {
        //javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255.0);
        int hue = (int) Math.round(fxColor.getHue());
        double saturation = fxColor.getSaturation();
        double brightness = fxColor.getBrightness();
        String s;
        String b;
        if (type == TYPE_INT || type == TYPE_DECIMAL) {
            s = roundNoZero(saturation, 2);
            b = roundNoZero(brightness, 2);
        } else {
            s = convertToPerInt(saturation);
            b = convertToPerInt(brightness);
        }
        String opacity = roundNoZero(fxColor.getOpacity(), 2);
        opacity = "1".equals(opacity) ? "1.0" : opacity;
        String result = hue + "," + s + "," + b;
        return withAlpha ? result + "," + opacity : result;
    }

    public static String toRgb(Color color, boolean withAlpha, int type) {
        String result;
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        if (type == TYPE_INT) {
            result = r + "," + g + "," + b;
        } else if (type == TYPE_DECIMAL) {
            result = roundNoZero(r / 255.0, 2) + ","
                    + roundNoZero(g / 255.0, 2) + ","
                    + roundNoZero(b / 255.0, 2);
        } else {
            result = convertToPerInt(roundNoZero(r / 255.0, 2)) + ","
                    + convertToPerInt(roundNoZero(g / 255.0, 2)) + ","
                    + convertToPerInt(roundNoZero(b / 255.0, 2));
        }
        String opacity = roundNoZero(color.getOpacity(), 2);
        opacity = "1".equals(opacity) ? "1.0" : opacity;
        return withAlpha ? result + "," + opacity : result;
    }

    private static String convertToPerInt(double value) {
        return Integer.parseInt(new DecimalFormat("0").format(value * 100)) + "%";
    }

    private static String convertToPerInt(String value) {
        return convertToPerInt(Double.parseDouble(value));
    }

    private static String roundNoZero(double value, int decimalPlaces) {
        BigDecimal decimal = new BigDecimal(String.format("%." + decimalPlaces + "f", value));
        BigDecimal noZeros = decimal.stripTrailingZeros();
        return noZeros.toPlainString();
    }
}
