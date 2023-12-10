package org.vmalibu.modules.settings;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public interface ModuleSettingsService {

    @NonNull Path getDataDir();

    @NonNull Path getSettingsDir();
}
