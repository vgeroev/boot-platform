package org.vmalibu.module.mathsroadmap.service.topic;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopicNode;
import org.vmalibu.modules.database.domainobject.DomainObject;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record TopicNodeDTO(long id,
                           int easinessLevel,
                           Set<Long> prevIds,
                           Set<Long> nextIds) {

    public static TopicNodeDTO from(@Nullable DBTopicNode topicNode) {
        if (topicNode == null) {
            return null;
        }

        return TopicNodeDTO.builder()
                .id(topicNode.getId())
                .easinessLevel(topicNode.getEasinessLevel())
                .prevIds(topicNode.getPrevNodes().stream().map(DomainObject::getId).collect(Collectors.toSet()))
                .nextIds(topicNode.getNextNodes().stream().map(DomainObject::getId).collect(Collectors.toSet()))
                .build();
    }
}
