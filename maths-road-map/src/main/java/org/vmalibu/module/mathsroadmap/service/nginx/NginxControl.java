package org.vmalibu.module.mathsroadmap.service.nginx;

import org.vmalibu.modules.module.exception.PlatformException;

public interface NginxControl {

    void reload() throws PlatformException;
}
