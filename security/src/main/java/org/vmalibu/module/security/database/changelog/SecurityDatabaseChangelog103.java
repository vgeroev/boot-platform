package org.vmalibu.module.security.database.changelog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;

@Component
public class SecurityDatabaseChangelog103 implements DatabaseChangelog {

    @Override
    public @NonNull String getModuleUuid() {
        return SecurityModuleConsts.UUID;
    }

    @Override
    public @NonNull String getVersion() {
        return "1.0.3";
    }

    @Override
    public @NonNull String getPath() {
        return "db/changelog/module/security/1.0.3/changelog.yaml";
    }
}
