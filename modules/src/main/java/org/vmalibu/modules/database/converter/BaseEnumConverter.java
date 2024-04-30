package org.vmalibu.modules.database.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.utils.BaseEnum;

import java.util.Optional;
import java.util.function.Function;

@Converter
public abstract class BaseEnumConverter<T extends BaseEnum> implements AttributeConverter<T, Short> {

    private final Function<Short, T> toBaseEnum;

    protected BaseEnumConverter(@NonNull Function<Short, T> toBaseEnum) {
        this.toBaseEnum = toBaseEnum;
    }

    @Override
    public Short convertToDatabaseColumn(T attribute) {
        return Optional.ofNullable(attribute).map(BaseEnum::shortValue).orElse(null);
    }

    @Override
    public T convertToEntityAttribute(Short dbData) {
        return toBaseEnum.apply(dbData);
    }
}
