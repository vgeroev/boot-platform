package org.vmalibu.module.security;

import java.util.Set;

public class SecurityModuleConsts {

    private SecurityModuleConsts() { }

    public static final Set<String> DEPENDENCIES = Set.of();

    public static final String UUID = "org.vmalibu.module.security";
    public static final String DB_PREFIX = "security_";
    public static final String REST_ANON_PREFIX = "/anon/security/v1";
    public static final String REST_AUTHORIZED_PREFIX = "/authorized/security/v1";

}
