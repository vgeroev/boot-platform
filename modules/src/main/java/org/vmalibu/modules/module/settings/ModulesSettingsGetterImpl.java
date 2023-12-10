package org.vmalibu.modules.module.settings;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.settings.ModuleSettingsService;
import org.vmalibu.modules.module.settings.rabbit.ClusterRabbitSettings;
import org.vmalibu.modules.utils.FilesUtils;
import org.vmalibu.modules.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ModulesSettingsGetterImpl implements ModulesSettingsGetter {

    private static final String CLUSTER_SETTINGS_DIR = "cluster";
    private static final String CLUSTER_RABBIT_SETTINGS_FILENAME = "rabbit.json";

    private final ClusterRabbitSettings clusterRabbitSettings;

    @Autowired
    public ModulesSettingsGetterImpl(ModuleSettingsService moduleSettingsService) throws PlatformException {
        Path settingsDir = moduleSettingsService.getSettingsDir();
        this.clusterRabbitSettings = loadClusterRabbitSettings(settingsDir);
    }

    @Override
    public @NonNull ClusterRabbitSettings getClusterRabbitSettings() {
        return clusterRabbitSettings;
    }

    private static ClusterRabbitSettings loadClusterRabbitSettings(Path configDir) throws PlatformException {
        Path clusterDir = configDir.resolve(CLUSTER_SETTINGS_DIR);
        FilesUtils.ensureDirectory(clusterDir);

        Path clusterPath = clusterDir.resolve(CLUSTER_RABBIT_SETTINGS_FILENAME);
        if (!Files.exists(clusterPath)) {
            try {
                Files.createFile(clusterPath);
                Files.writeString(clusterPath, ClusterRabbitSettings.getDefault().toString());
            } catch (IOException e) {
                throw GeneralExceptionBuilder.buildIOErrorException(e);
            }
        }

        return ClusterRabbitSettings.load(JsonUtils.readJson(clusterPath));
    }

}
