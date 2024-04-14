package org.vmalibu.module.mathsroadmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vmalibu.module.mathsroadmap.service.topic.TopicService;
import org.vmalibu.module.security.SecurityModule;
import org.vmalibu.modules.module.AbstractModule;

import java.util.Set;

@Component
public class MathsRoadMapModule extends AbstractModule<MathsRoadMapConfig> {

    public MathsRoadMapModule() {
        super(buildConfig());
    }

    private static MathsRoadMapConfig buildConfig() {
        return new MathsRoadMapConfig.Builder(MathsRoadMapConsts.UUID, Set.of(SecurityModule.class))
                .build();
    }

    @Autowired
    private TopicService topicService;

}
