package org.vmalibu.modules.settings;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.FilesUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ModuleSettingsServiceImpl implements ModuleSettingsService {

    private static final String OPTION_DATA_DIR = "data_dir";
    private static final String OPTION_SETTINGS_DIR = "settings_dir";

    private final Path dataDirPath;
    private final Path settingsDirPath;

    @Autowired
    public ModuleSettingsServiceImpl(ApplicationArguments applicationArguments) throws PlatformException {
        this.dataDirPath =
                getPathFromArguments(applicationArguments, OPTION_DATA_DIR, ModuleSettingsServiceImpl::getDefaultDataDir);
        this.settingsDirPath =
                getPathFromArguments(applicationArguments, OPTION_SETTINGS_DIR, () -> getDefaultSettingsDir(dataDirPath));

        FilesUtils.ensureDirectory(dataDirPath);
        FilesUtils.ensureDirectory(settingsDirPath);
    }

    @Override
    public @NonNull Path getDataDir() {
        return dataDirPath;
    }

    @Override
    public @NonNull Path getSettingsDir() {
        return settingsDirPath;
    }

    private static Path getPathFromArguments(ApplicationArguments applicationArguments,
                                             String argumentOption,
                                             Supplier<Path> orElse) {
        List<String> values = applicationArguments.getOptionValues(argumentOption);
        if (values != null) {
            if (values.size() != 1) {
                throw new IllegalStateException("Invalid options count for %s".formatted(argumentOption));
            }

            return Path.of(values.get(0)).toAbsolutePath();
        }

        return orElse.get();
    }

    private static Path getDefaultDataDir() {
        return Path.of("data").toAbsolutePath();
    }

    private static Path getDefaultSettingsDir(Path dataDirPath) {
        return dataDirPath.resolve("settings").toAbsolutePath();
    }


}
