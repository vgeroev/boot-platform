package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ArticlePageManager {

    @NonNull String createPreviewPageByTeX4ht(@NonNull String latex,
                                              @NonNull String userId,
                                              @Nullable String configuration) throws PlatformException;

}
