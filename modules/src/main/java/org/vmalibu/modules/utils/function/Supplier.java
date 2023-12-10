package org.vmalibu.modules.utils.function;

import org.vmalibu.modules.module.exception.PlatformException;

@FunctionalInterface
public interface Supplier<T> {

    T get() throws PlatformException;
}
