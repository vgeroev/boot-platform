package org.vmalibu.modules.database.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.utils.BaseEnum;

import java.util.Optional;
import java.util.function.Function;

@Converter
public abstract class BaseEnumConverter<T extends BaseEnum> implements AttributeConverter<T, Integer> {

    private final Function<Integer, T> toBaseEnum;

    protected BaseEnumConverter(@NonNull Function<Integer, T> toBaseEnum) {
        this.toBaseEnum = toBaseEnum;
    }

    @Override
    public Integer convertToDatabaseColumn(T attribute) {
        return Optional.ofNullable(attribute).map(BaseEnum::intValue).orElse(null);
    }

    @Override
    public T convertToEntityAttribute(Integer dbData) {
        return toBaseEnum.apply(dbData);
    }
}
