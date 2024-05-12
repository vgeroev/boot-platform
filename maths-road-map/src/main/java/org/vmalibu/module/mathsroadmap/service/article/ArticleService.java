package org.vmalibu.module.mathsroadmap.service.article;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Set;

public interface ArticleService {

    @Nullable ArticleDTO findArticle(long id);

    @NonNull ArticleDTO create(@NonNull String title,
                               @NonNull String latex,
                               @Nullable String configuration,
                               @NonNull AbstractionLevel abstractionLevel,
                               @NonNull Set<Long> prevNodeIds,
                               @NonNull Set<Long> nextNodeIds) throws PlatformException;

    void updateNode(long id,
                    @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                    @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException;

    void addNodes(long id,
                  @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                  @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException;

}
