package org.vmalibu.module.core.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexColorUtilsTest {

    @Test
    @DisplayName("Test Case: Parsing hex color when argument is incorrect. Awaiting null")
    void parseWhenHexColorIsIncorrectTest() {
        // Invalid symbol "-"
        Assertions.assertThat(HexColorUtils.parse("00-ffaa")).isNull();
        // Too few symbols
        Assertions.assertThat(HexColorUtils.parse("0ffaa")).isNull();
        // Invalid symbol "z"
        Assertions.assertThat(HexColorUtils.parse("01ffaz")).isNull();
        // Too many symbols
        Assertions.assertThat(HexColorUtils.parse("01ffabc")).isNull();
    }

    @Test
    @DisplayName("Test Case: Parsing hex color. Awaiting correct decimal representation")
    void parseTest() {
        String hex1 = "01afAF";
        Assertions.assertThat(HexColorUtils.parse(hex1)).isEqualTo(toDecimalFromHex(hex1));
        String hex2 = "FFFFFF";
        Assertions.assertThat(HexColorUtils.parse(hex2)).isEqualTo(toDecimalFromHex(hex2));
        String hex3 = "000000";
        Assertions.assertThat(HexColorUtils.parse(hex3)).isEqualTo(toDecimalFromHex(hex3));
        String hex4 = "ABCDEF";
        Assertions.assertThat(HexColorUtils.parse(hex4)).isEqualTo(toDecimalFromHex(hex4));
    }

    private static int toDecimalFromHex(String hex) {
        byte[] bytes = hex.getBytes();
        int result = 0;
        int powed = 1;
        for (int i = bytes.length - 1; i > -1; i--) {
            byte x = bytes[i];
            int value;
            if (x >= '0' && x <= '9') {
                value = x - 48;
            } else if (x >= 'a' && x <= 'z') {
                value = x - 87;
            } else if (x >= 'A' && x <= 'Z') {
                value = x - 55;
            } else {
                throw new IllegalStateException("Invalid byte value = " + x);
            }

            result += powed * value;
            powed *= 16;
        }

        return result;
    }
}
