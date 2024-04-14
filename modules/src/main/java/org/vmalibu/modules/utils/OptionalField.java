package org.vmalibu.modules.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalField<T> {

    public static final OptionalField<?> EMPTY_FIELD = new OptionalField<>(null, false);

    private final T value;
    private final boolean present;

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return present;
    }

    public void ifPresent(@NonNull Consumer<? super T> consumer) {
        if (present) {
            consumer.accept(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull OptionalField<T> empty() {
        return (OptionalField<T>) EMPTY_FIELD;
    }

    public static <T> @NonNull OptionalField<T> of(@Nullable T value) {
        return new OptionalField<>(value, true);
    }

}
