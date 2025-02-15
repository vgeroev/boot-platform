package org.vmalibu.module.security.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;
import org.vmalibu.modules.utils.Version;

@Component
public class SecurityDatabaseChangelog107 implements DatabaseChangelog {

    @Override
    public @NonNull String getModuleUuid() {
        return SecurityModuleConsts.UUID;
    }

    @Override
    public @NonNull Version getVersion() {
        return new Version(1, 0, 7);
    }

    @Override
    public @NonNull String getPath() {
        return "db/changelog/module/security/1.0.7/changelog.yaml";
    }
}
