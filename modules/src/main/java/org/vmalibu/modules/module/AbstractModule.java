package org.vmalibu.modules.module;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Objects;

public abstract class AbstractModule<T extends ModuleConfig> {

    protected final T config;

    protected AbstractModule(@NonNull T config) {
        this.config = Objects.requireNonNull(config);
    }

    public @NonNull T getConfig() {
        return config;
    }

    public void onStart() throws PlatformException { }

    public void onDestroy() throws PlatformException { }
}
