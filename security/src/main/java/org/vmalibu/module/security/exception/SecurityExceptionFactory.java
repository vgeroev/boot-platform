package org.vmalibu.module.security.exception;

import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;

public class SecurityExceptionFactory {

    public static final String NO_PRIVILEGE_ACCESS_OPS = "no_privilege_access_ops";
    public static final String INVALID_PRIVILEGE_KEY = "invalid_privilege_key";
    public static final String INVALID_PRIVILEGE_ACCESS_OPS = "invalid_privilege_access_ops";

    private SecurityExceptionFactory() { }

    public static PlatformException buildNoPrivilegeAccessOpsException(String key) {
        return new PlatformException(NO_PRIVILEGE_ACCESS_OPS, Map.of("key", key));
    }

    public static PlatformException buildInvalidPrivilegeKeyException(String key) {
        return new PlatformException(INVALID_PRIVILEGE_KEY, Map.of("key", key));
    }

    public static PlatformException buildInvalidPrivilegeAccessOpsException() {
        return new PlatformException(INVALID_PRIVILEGE_ACCESS_OPS);
    }
}
