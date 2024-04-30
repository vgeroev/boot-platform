package org.vmalibu.module.mathsroadmap.service.config;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface MathsRoadMapConfigService {

    @NonNull NginxConfig getNginxConfig();
}
