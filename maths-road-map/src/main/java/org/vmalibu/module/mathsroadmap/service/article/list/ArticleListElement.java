package org.vmalibu.module.mathsroadmap.service.article.list;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.security.service.user.UserDTO;

import java.util.Date;
import java.util.Set;

@Builder
public record ArticleListElement(long id,
                                 Date createdAt,
                                 Date updatedAt,
                                 UserDTO creator,
                                 String title,
                                 String description,
                                 int likes,
                                 int dislikes,
                                 Set<Long> tagIds) {

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
                .likes(article.getLikes())
                .dislikes(article.getDislikes())
                .tagIds(Set.of(article.getTagIds()))
                .build();
    }

}
