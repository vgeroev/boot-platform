package org.vmalibu.module.mathsroadmap.service.article;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.security.service.user.UserDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
public record ArticlePageDTO(long id,
                             Date createdAt,
                             Date updatedAt,
                             UserDTO creator,
                             String title,
                             String description,
                             int likes,
                             int dislikes,
                             List<TagDTO> tags) {

    public static ArticlePageDTO from(@Nullable DBArticle article) {
        if (article == null) {
            return null;
        }

        Set<DBTag> dbTags = article.getTags();
        List<TagDTO> tags = new ArrayList<>(dbTags.size());
        for (DBTag dbTag : dbTags) {
            tags.add(TagDTO.from(dbTag));
        }

        return ArticlePageDTO.builder()
                .id(article.getId())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .creator(UserDTO.from(article.getCreator()))
                .title(article.getTitle())
                .description(article.getDescription())
                .likes(article.getLikes())
                .dislikes(article.getDislikes())
                .tags(tags)
                .build();
    }

}

