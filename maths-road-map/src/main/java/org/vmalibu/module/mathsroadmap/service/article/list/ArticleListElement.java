package org.vmalibu.module.mathsroadmap.service.article.list;

import lombok.Builder;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;

@Builder
public record ArticleListElement(ArticleDTO articleDTO,
                                 String uri) {
}
