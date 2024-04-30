package org.vmalibu.module.mathsroadmap.service.article;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;

@Builder
public record ArticleDTO(long id,
                         AbstractionLevel abstractionLevel,
                         String title) {

    public static ArticleDTO from(@Nullable DBArticle article) {
        if (article == null) {
            return null;
        }

        return ArticleDTO.builder()
                .id(article.getId())
                .abstractionLevel(article.getAbstractionLevel())
                .title(article.getTitle())
                .build();
    }

}
