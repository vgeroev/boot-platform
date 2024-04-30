package org.vmalibu.module.mathsroadmap.service.config;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;

public record NginxConfig(@NonNull URL url,
                          @NonNull String reloadCmd,
                          @NonNull NginxArticlesConfig nginxArticlesConfig) {
}
