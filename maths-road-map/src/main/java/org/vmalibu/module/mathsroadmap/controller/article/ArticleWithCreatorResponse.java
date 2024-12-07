package org.vmalibu.module.mathsroadmap.controller.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.vmalibu.module.mathsroadmap.service.article.ArticleWithCreatorDTO;

@Data
@AllArgsConstructor
public class ArticleWithCreatorResponse {

    public static final String JSON_ARTICLE = "article";
    public static final String JSON_ARTICLE_URL = "url";

    @JsonProperty(JSON_ARTICLE)
    private ArticleWithCreatorDTO article;

    @JsonProperty(JSON_ARTICLE_URL)
    private String articleURL;
}
