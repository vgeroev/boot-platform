package org.vmalibu.module.mathsroadmap;

import org.vmalibu.module.security.SecurityModuleConsts;

import java.util.Set;

public class MathsRoadMapConsts {

    private MathsRoadMapConsts() { }

    public static final Set<String> DEPENDENCIES = Set.of(SecurityModuleConsts.UUID);

    public static final String UUID = "org.vmalibu.module.mathsroadmap";
    public static final String DB_PREFIX = "maths_road_map_";
    public static final String REST_AUTHORIZED_PREFIX = "/authorized/maths-road-map/v1";
    public static final String REST_ANON_PREFIX = "/anon/maths-road-map/v1";

}
