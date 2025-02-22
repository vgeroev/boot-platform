package org.vmalibu.module.core.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;
import org.vmalibu.modules.utils.Version;

import java.util.Set;

@Component
public class CoreDatabaseChangelog1014 implements DatabaseChangelog {

    @Override
    public @NonNull String getModuleUuid() {
        return CoreConsts.UUID;
    }

    @Override
    public @NonNull Version getVersion() {
        return new Version(1, 0, 14);
    }

    @Override
    public @NonNull Set<String> getDependencies() {
        return CoreConsts.DEPENDENCIES;
    }

    @Override
    public @NonNull String getPath() {
        return "db/changelog/module/core/1.0.14/changelog.yaml";
    }
}
