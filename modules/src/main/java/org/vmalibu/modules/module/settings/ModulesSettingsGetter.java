package org.vmalibu.modules.module.settings;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.settings.rabbit.ClusterRabbitSettings;

public interface ModulesSettingsGetter {

    @NonNull ClusterRabbitSettings getClusterRabbitSettings();
}
