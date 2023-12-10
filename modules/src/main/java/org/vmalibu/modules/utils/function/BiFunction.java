package org.vmalibu.modules.utils.function;

import org.vmalibu.modules.module.exception.PlatformException;

@FunctionalInterface
public interface BiFunction<T, U, V> {

    V apply(T t, U u) throws PlatformException;
}
