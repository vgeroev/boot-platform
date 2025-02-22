package org.vmalibu.module.core.utils;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;

@UtilityClass
public class HexColorUtils {

    // Hex number valid segment = [00_00_00, FF_FF_FF]
    private static final int MAX_COLOR_NUMBER = 256 * 256 * 256 - 1;

    public static @Nullable Integer parse(@Nullable String hexColor) {
        if (hexColor == null || hexColor.length() != 6) {
            return null;
        }

        int color;
        try {
            color = Integer.parseInt(hexColor, 16);
        } catch (NumberFormatException e) {
            return null;
        }

        return color;
    }

    public static boolean isInColorRange(int color) {
        return color >= 0 && color <= MAX_COLOR_NUMBER;
    }

}
