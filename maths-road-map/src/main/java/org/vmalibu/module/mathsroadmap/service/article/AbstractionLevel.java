package org.vmalibu.module.mathsroadmap.service.article;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.utils.BaseEnum;

public enum AbstractionLevel implements BaseEnum {

    SUPREME(1),
    MEDIUM(100),
    LOW(200);

    private final short id;
    AbstractionLevel(int id) {
        this.id = (short) id;
    }

    @Override
    public short shortValue() {
        return id;
    }

    public static @Nullable AbstractionLevel from(@Nullable Short value) {
        if (value == null) {
            return null;
        }

        for (AbstractionLevel level : values()) {
            if (level.id == value) {
                return level;
            }
        }

        throw new IllegalArgumentException("Invalid key=" + value);
    }
}
