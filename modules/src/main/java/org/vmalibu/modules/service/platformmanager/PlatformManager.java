package org.vmalibu.modules.service.platformmanager;

import org.vmalibu.modules.module.exception.PlatformException;

public interface PlatformManager {

    void start() throws PlatformException;

    void destroy() throws PlatformException;
}
