package org.vmalibu.modules.service.platformmanager;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.AbstractModule;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.List;

public interface PlatformManager {

    void start() throws PlatformException;

    void destroy() throws PlatformException;

    @NonNull List<? extends AbstractModule<?>> getModules();
}
