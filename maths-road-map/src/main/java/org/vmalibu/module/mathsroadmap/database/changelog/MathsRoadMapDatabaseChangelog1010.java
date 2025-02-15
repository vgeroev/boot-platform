package org.vmalibu.module.mathsroadmap.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;
import org.vmalibu.modules.utils.Version;

import java.util.Set;

@Component
public class MathsRoadMapDatabaseChangelog1010 implements DatabaseChangelog {

    @Override
    public @NonNull String getModuleUuid() {
        return MathsRoadMapConsts.UUID;
    }

    @Override
    public @NonNull Set<String> getDependencies() {
        return MathsRoadMapConsts.DEPENDENCIES;
    }

    @Override
    public @NonNull Version getVersion() {
        return new Version(1, 0, 10);
    }

    @Override
    public @NonNull String getPath() {
        return "db/changelog/module/mathsroadmap/1.0.10/changelog.yaml";
    }
}
