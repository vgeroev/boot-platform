package org.vmalibu.module.mathsroadmap.service.article;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ArticleService {

    @Nullable ArticleDTO findArticle(long id);

    @NonNull PaginatedDto<ArticleDTO> findAll(@NonNull ArticlePagingRequest pagingRequest);

    @NonNull ArticleDTO create(@NonNull String title,
                               @NonNull String latex,
                               @Nullable String configuration,
                               @NonNull AbstractionLevel abstractionLevel,
                               @NonNull UserSource userSource) throws PlatformException;

}
