package org.vmalibu.module.core;

import lombok.experimental.UtilityClass;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class TestUtils {

    public <T extends Enum<T>> T getRandomEnumValue(Class<T> clazz, Predicate<T> filter) {
        List<T> enumConstants = Stream.of(clazz.getEnumConstants())
                .filter(filter)
                .toList();

        if (enumConstants.isEmpty()) {
            return null;
        }

        int index = RandomUtils.nextInt(0, enumConstants.size());
        return enumConstants.get(index);
    }

    public <T extends Enum<T>> T getRandomEnumValue(Class<T> clazz) {
        return getRandomEnumValue(clazz, value -> true);
    }
}
