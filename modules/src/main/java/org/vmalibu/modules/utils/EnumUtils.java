package org.vmalibu.modules.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class EnumUtils {

    private EnumUtils() { }

    public static <T extends Enum<T>> T parseOrGetDefault(@NonNull Class<T> tEnum,
                                                          @NonNull String rawValue,
                                                          @NonNull Supplier<T> supplier) {
        return Stream.of(tEnum.getEnumConstants())
                .filter(value -> value.toString().equals(rawValue))
                .findFirst()
                .orElseGet(supplier);
    }

    public static <T extends Enum<T>, X extends Throwable> T parseOrThrow(@NonNull Class<T> tEnum,
                                                                          @NonNull String rawValue,
                                                                          @NonNull Supplier<X> supplier) throws X {
        return Stream.of(tEnum.getEnumConstants())
                .filter(value -> value.toString().equals(rawValue))
                .findFirst()
                .orElseThrow(supplier);
    }
}
