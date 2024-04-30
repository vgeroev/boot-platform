package org.vmalibu.module.mathsroadmap.service.config;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public record NginxArticlesConfig(@NonNull Path previewConfDir, @NonNull Path previewDir) {
}
