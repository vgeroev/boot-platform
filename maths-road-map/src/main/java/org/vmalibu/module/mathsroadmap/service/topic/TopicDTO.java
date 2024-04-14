package org.vmalibu.module.mathsroadmap.service.topic;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopic;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopicNode;

@Builder
public record TopicDTO(long id,
                       TopicNodeDTO node,
                       String title,
                       String body) {

    public static TopicDTO from(@Nullable DBTopic topic) {
        if (topic == null) {
            return null;
        }

        return TopicDTO.builder()
                .id(topic.getId())
                .node(TopicNodeDTO.from(topic.getNode()))
                .title(topic.getTitle())
                .body(topic.getBody())
                .build();
    }

    public static TopicDTO from(@Nullable DBTopicNode topicNode) {
        if (topicNode == null) {
            return null;
        }

        return from(topicNode.getTopic());
    }
}
