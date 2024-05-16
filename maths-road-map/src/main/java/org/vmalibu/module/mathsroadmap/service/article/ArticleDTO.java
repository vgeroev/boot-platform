package org.vmalibu.module.mathsroadmap.service.article;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;

import java.util.Date;

@Builder
public record ArticleDTO(long id,
                         Date createdAt,
                         Date updatedAt,
                         String creatorUsername,
                         AbstractionLevel abstractionLevel,
                         String title) {

    public static ArticleDTO from(@Nullable DBArticle article) {
        if (article == null) {
            return null;
        }

        return ArticleDTO.builder()
                .id(article.getId())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .creatorUsername(article.getCreatorUsername())
                .abstractionLevel(article.getAbstractionLevel())
                .title(article.getTitle())
                .build();
    }

}
