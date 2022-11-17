package com.leewyatt.fxtools.utils.convert;

public class RgbHslConverter {
    /**
     * @param rgb
     * @return
     */
    public static HSL RGB2HSL(RGB rgb) {
        if (rgb == null) {
            return null;
        }
        float H, S, L, varMin, varMax, delMax, delR, delG, delB;
        H = 0;
        varMin = Math.min(rgb.red, Math.min(rgb.blue, rgb.green));
        varMax = Math.max(rgb.red, Math.max(rgb.blue, rgb.green));
        delMax = varMax - varMin;
        L = (varMax + varMin) / 2;
        if (delMax == 0) {
            H = 0;
            S = 0;
        } else {
            if (L < 128) {
                S = 256 * delMax / (varMax + varMin);
            } else {
                S = 256 * delMax / (512 - varMax - varMin);
            }
            delR = ((360 * (varMax - rgb.red) / 6) + (360 * delMax / 2))
                    / delMax;
            delG = ((360 * (varMax - rgb.green) / 6) + (360 * delMax / 2))
                    / delMax;
            delB = ((360 * (varMax - rgb.blue) / 6) + (360 * delMax / 2))
                    / delMax;
            if (rgb.red == varMax) {
                H = delB - delG;
            } else if (rgb.green == varMax) {
                H = 120 + delR - delB;
            } else if (rgb.blue == varMax) {
                H = 240 + delG - delR;
            }
            if (H < 0) {
                H += 360;
            }
            if (H >= 360) {
                H -= 360;
            }
            if (L >= 256) {
                L = 255;
            }
            if (S >= 256) {
                S = 255;
            }
        }
        return new HSL(H, S, L);
    }

    /**
     * @param hsl
     * @return
     */
    public static RGB HSL2RGB(HSL hsl) {
        if (hsl == null) {
            return null;
        }
        float H = hsl.getH();
        float S = hsl.getS();
        float L = hsl.getL();

        float R, G, B, var_1, var_2;
        if (S == 0) {
            R = L;
            G = L;
            B = L;
        } else {
            if (L < 128) {
                var_2 = (L * (256 + S)) / 256;
            } else {
                var_2 = (L + S) - (S * L) / 256;
            }
            if (var_2 > 255) {
                var_2 = Math.round(var_2);
            }
            if (var_2 > 254) {
                var_2 = 255;
            }
            var_1 = 2 * L - var_2;
            R = RGBFromHue(var_1, var_2, H + 120);
            G = RGBFromHue(var_1, var_2, H);
            B = RGBFromHue(var_1, var_2, H - 120);
        }
        R = R < 0 ? 0 : R;
        R = R > 255 ? 255 : R;
        G = G < 0 ? 0 : G;
        G = G > 255 ? 255 : G;
        B = B < 0 ? 0 : B;
        B = B > 255 ? 255 : B;
        return new RGB((int) Math.round(R), (int) Math.round(G), (int) Math.round(B));
    }

    /**
     * @param a
     * @param b
     * @param h
     * @return
     */
    public static float RGBFromHue(float a, float b, float h) {
        if (h < 0) {
            h += 360;
        }
        if (h >= 360) {
            h -= 360;
        }
        if (h < 60) {
            return a + ((b - a) * h) / 60;
        }
        if (h < 180) {
            return b;
        }

        if (h < 240) {
            return a + ((b - a) * (240 - h)) / 60;
        }
        return a;
    }

    //public static void main(String[] args) {
    //    HSL hsl = RGB2HSL(new RGB(64, 191, 64));
    //    System.out.println(hsl.toString());
    //    RGB rgb = HSL2RGB(new HSL(120.0f, 128f, 127.5f));
    //    System.out.println(rgb.toString());
    //}

}