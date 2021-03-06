package com.nopo.game;

import com.badlogic.gdx.math.Interpolation;

public class Utils {
    //stole from https://www.demo2s.com/java/java-string-getline-string-s-int-line.html
    public static String getLine(String s, int line) {
        String[] lines = s.split("\r\n|\n|\r");
        if (lines.length >= line) {
            return lines[line - 1];
        } else {
            return null;
        }
    }

    public static int getLineLength(String s) {
        String[] lines = s.split("\r\n|\n|\r");
        return lines.length;
    }

    // Yoinked from https://commons.apache.org/proper/commons-lang/apidocs/src-html/org/apache/commons/lang3/StringUtils.html#line.5988
    public static String removeEnd(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static boolean interpUp = true;
    static Interpolation easAlpha = Interpolation.swing;
    static int lifeTime = 2;
    static Float elapsed = 0f;

    //https://libgdx.com/wiki/math-utils/interpolation
    public static float interp(Interpolation interpolation, float delta) {
        if (elapsed < 2 && interpUp) {
            elapsed += delta;
        } else if (elapsed > 0) {
            elapsed -= delta;
            interpUp = false;
        } else {
            interpUp = true;
        }
        float progress = Math.min(1f, elapsed / lifeTime);   // 0 -> 1
        return interpolation.apply(progress);
    }

    public static float clampWithWrap(float value, float min, float max) {
        if (value < min) return max;
        if (value > max) return min;
        return value;
    }
}
