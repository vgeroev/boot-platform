package org.vmalibu.modules.utils.function;

import org.vmalibu.modules.module.exception.PlatformException;

@FunctionalInterface
public interface Function<T, U> {

    U apply(T t) throws PlatformException;
}
