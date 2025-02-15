package org.vmalibu.module.mathsroadmap.service.article;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.security.service.user.UserDTO;

import java.util.Date;

@Builder
public record ArticleWithCreatorDTO(long id,
                                    Date createdAt,
                                    Date updatedAt,
                                    UserDTO creator,
                                    String title,
                                    String description,
                                    int likes,
                                    int dislikes) {

    public static ArticleWithCreatorDTO from(@Nullable DBArticle article) {
        if (article == null) {
            return null;
        }

        return ArticleWithCreatorDTO.builder()
                .id(article.getId())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .creator(UserDTO.from(article.getCreator()))
                .title(article.getTitle())
                .description(article.getDescription())
                .likes(article.getLikes())
                .dislikes(article.getDislikes())
                .build();
    }

}

