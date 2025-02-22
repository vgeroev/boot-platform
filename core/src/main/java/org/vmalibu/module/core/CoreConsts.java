package org.vmalibu.module.core;

import org.vmalibu.module.security.SecurityModuleConsts;

import java.util.Set;

public class CoreConsts {

    private CoreConsts() { }

    public static final Set<String> DEPENDENCIES = Set.of(SecurityModuleConsts.UUID);

    public static final String UUID = "org.vmalibu.module.core";
    public static final String DB_PREFIX = "core_";
    public static final String REST_AUTHORIZED_PREFIX = "/authorized/core/v1";
    public static final String REST_ANON_PREFIX = "/anon/core/v1";

}
