package org.vmalibu.module.mathsroadmap.utils;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URI;

@UtilityClass
public class ArticleURIUtils {

    public static @NonNull URI getArticleURI(@NonNull URI serverURI, long articleId) {
        return serverURI.resolve("/articles/%d/".formatted(articleId));
    }

    public static @NonNull URI getArticlePreviewURI(@NonNull URI serverURI, @NonNull String dirname) {
        return serverURI.resolve("/articles/preview/%s/".formatted(dirname));
    }
}
