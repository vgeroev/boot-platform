package org.vmalibu.module.exercises.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TestUtils {

    private TestUtils() { }

    public static <T extends Enum<T>> @Nullable T getRandomEnumValue(@NonNull Class<T> enumClass,
                                                                     @NonNull Predicate<T> filter) {
        List<T> enumConstants = Stream.of(enumClass.getEnumConstants())
                .filter(filter)
                .toList();

        int size = enumConstants.size();
        if (size == 0) {
            return null;
        }

        int index = new Random().nextInt(size);
        return enumConstants.get(index);
    }

    public static <T extends Enum<T>> @Nullable T getRandomEnumValue(@NonNull Class<T> enumClass) {
        return getRandomEnumValue(enumClass, value -> true);
    }
}
