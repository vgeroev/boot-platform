package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.service.article.AbstractionLevel;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.modules.module.exception.PlatformException;

import java.net.URI;
import java.util.Set;

public interface ArticlePageManager {

    @NonNull URI createPreviewByTeX4ht(@NonNull String latex,
                                       @NonNull String userId,
                                       @Nullable String configuration) throws PlatformException;

    @NonNull ArticleDTO createByTeX4ht(@NonNull String title,
                                       @NonNull String latex,
                                       @Nullable String configuration,
                                       @NonNull AbstractionLevel abstractionLevel,
                                       @NonNull Set<Long> prevArticleIds,
                                       @NonNull Set<Long> nextArticleIds,
                                       @NonNull String userId) throws PlatformException;

    @NonNull URI getArticleURI(long articleId);
}
