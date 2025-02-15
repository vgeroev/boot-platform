package org.vmalibu.modules.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.utils.Version;

import java.util.Set;

public interface DatabaseChangelog {

    @NonNull String getModuleUuid();

    @NonNull Version getVersion();

    default @NonNull Set<String> getDependencies() {
        return Set.of();
    }

    @NonNull String getPath();
}
