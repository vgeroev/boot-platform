package org.vmalibu.module.mathsroadmap.service.topic;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Set;

public interface TopicService {

    @Nullable TopicDTO findTopic(long id);

    @NonNull TopicDTO create(@NonNull String title,
                             @NonNull String body,
                             int easinessLevel,
                             @NonNull Set<Long> prevNodeIds,
                             @NonNull Set<Long> nextNodeIds) throws PlatformException;

    void updateNode(long id,
                    @NonNull OptionalField<@NonNull Integer> easinessLevel,
                    @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                    @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException;

    void addNodes(long id,
                  @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                  @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException;

    void updateTopicTitle(long id, @NonNull String title) throws PlatformException;

    void updateTopicBody(long id, @NonNull String body) throws PlatformException;
}
