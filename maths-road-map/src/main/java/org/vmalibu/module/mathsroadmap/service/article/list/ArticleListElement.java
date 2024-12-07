package org.vmalibu.module.mathsroadmap.service.article.list;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.security.service.user.UserDTO;

import java.util.Date;

@Builder
public record ArticleListElement(long id,
                                Date createdAt,
                                Date updatedAt,
                                UserDTO creator,
                                String title,
                                String description) {

    public static ArticleListElement from(@Nullable DBArticle article) {
        if (article == null) {
            return null;
        }

        return ArticleListElement.builder()
                .id(article.getId())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .creator(UserDTO.from(article.getCreator()))
                .title(article.getTitle())
                .description(article.getDescription())
                .build();
    }

}
