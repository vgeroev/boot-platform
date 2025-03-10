package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.net.URI;

public interface ArticlePageManager {

    @NonNull URI createPreviewByTeX4ht(@NonNull String latex,
                                       @Nullable String configuration,
                                       @NonNull UserSource userSource) throws PlatformException;

    @NonNull ArticleDTO createByTeX4ht(@NonNull String title,
                                       @Nullable String description,
                                       @NonNull String latex,
                                       @Nullable String configuration,
                                       @NonNull UserSource userSource) throws PlatformException;

    @NonNull ArticleDTO updateByTeX4ht(long id,
                                       OptionalField<@NonNull String> latex,
                                       OptionalField<@Nullable String> configuration,
                                       @NonNull UserSource userSource) throws PlatformException;

    @NonNull URI getArticleURI(long articleId);
}
