package org.vmalibu.module.mathsroadmap.service.article;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListTags;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

public interface ArticleService {

    @Nullable ArticleDTO findArticle(long id);

    @Nullable
    ArticlePageDTO findArticlePage(long id);

    @Nullable ArticleLatexDTO findArticleLatex(long id);

    @NonNull ArticleListTags findAll(@NonNull ArticlePagingRequest pagingRequest);

    @NonNull ArticleDTO create(@NonNull String title,
                               @Nullable String description,
                               @NonNull String latex,
                               @Nullable String configuration,
                               @NonNull UserSource userSource) throws PlatformException;

    @NonNull ArticleDTO update(long id,
                               OptionalField<@NonNull String> title,
                               OptionalField<@Nullable String> description,
                               @NonNull UserSource userSource) throws PlatformException;

    @NonNull ArticleDTO updateLatex(long id,
                                    OptionalField<@NonNull String> latex,
                                    OptionalField<@Nullable String> configuration,
                                    @NonNull UserSource userSource) throws PlatformException;

    @NonNull ArticleUserLikeAction like(long id, long userId) throws PlatformException;

    @NonNull ArticleUserLikeAction dislike(long id, long userId) throws PlatformException;

    @NonNull ArticleUserLikeAction findArticleLikeAction(long id, long userId);
}
