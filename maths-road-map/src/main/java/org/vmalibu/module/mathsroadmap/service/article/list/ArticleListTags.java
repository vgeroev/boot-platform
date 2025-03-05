package org.vmalibu.module.mathsroadmap.service.article.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.modules.database.paging.PaginatedDto;

import java.util.List;

public record ArticleListTags(@NonNull PaginatedDto<ArticleListElement> articles, @NonNull List<TagDTO> tags) {

}
