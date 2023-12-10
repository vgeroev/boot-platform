package org.vmalibu.modules.utils.function;

import org.vmalibu.modules.module.exception.PlatformException;

@FunctionalInterface
public interface Consumer<T> {

    void accept(T t) throws PlatformException;
}
