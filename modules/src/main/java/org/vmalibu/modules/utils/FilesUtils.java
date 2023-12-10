package org.vmalibu.modules.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesUtils {

    private FilesUtils() { }

    public static void ensureDirectory(@NonNull Path dir) throws PlatformException {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                throw GeneralExceptionBuilder.buildIOErrorException(e);
            }
        }

        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException(dir + " is not a directory.");
        }
    }

}
