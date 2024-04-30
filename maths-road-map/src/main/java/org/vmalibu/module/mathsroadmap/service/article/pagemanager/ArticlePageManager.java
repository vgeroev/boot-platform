package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ArticlePageManager {

    @NonNull String createPreviewPage(@NonNull String latex, @NonNull String userId) throws PlatformException;

}
