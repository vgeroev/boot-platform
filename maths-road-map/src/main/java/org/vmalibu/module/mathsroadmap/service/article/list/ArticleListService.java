package org.vmalibu.module.mathsroadmap.service.article.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.database.paging.PaginatedDto;

public interface ArticleListService {

    @NonNull PaginatedDto<ArticleListElement> findAll(@NonNull ArticlePagingRequest pagingRequest);
}
