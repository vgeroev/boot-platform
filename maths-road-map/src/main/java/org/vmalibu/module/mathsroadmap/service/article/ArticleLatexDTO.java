package org.vmalibu.module.mathsroadmap.service.article;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;

@Builder
public record ArticleLatexDTO(long id,
                              String latex,
                              String configuration) {

    public static @Nullable ArticleLatexDTO from(@Nullable DBArticleLatex articleLatex) {
        if (articleLatex == null) {
            return null;
        }

        return ArticleLatexDTO.builder()
                .id(articleLatex.getId())
                .latex(articleLatex.getLatex())
                .configuration(articleLatex.getConfiguration())
                .build();
    }
}
