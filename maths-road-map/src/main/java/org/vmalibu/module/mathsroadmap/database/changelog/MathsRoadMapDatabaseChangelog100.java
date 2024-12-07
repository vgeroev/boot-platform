package org.vmalibu.module.mathsroadmap.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;

import java.util.Set;

@Component
public class MathsRoadMapDatabaseChangelog100 implements DatabaseChangelog {

    @Override
    public @NonNull String getModuleUuid() {
        return MathsRoadMapConsts.UUID;
    }

    @Override
    public @NonNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NonNull Set<String> getDependencies() {
        return Set.of(SecurityModuleConsts.UUID);
    }

    @Override
    public @NonNull String getPath() {
        return "db/changelog/module/mathsroadmap/1.0.0/changelog.yaml";
    }
}
