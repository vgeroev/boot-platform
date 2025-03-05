package org.vmalibu.module.mathsroadmap.service.tag;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.vmalibu.module.core.service.tag.TagActionsListener;
import org.vmalibu.module.mathsroadmap.service.article.ArticleTagService;

import java.util.Set;

@Service
@AllArgsConstructor
public class TagActionsListenerImpl implements TagActionsListener {

    private final ArticleTagService articleTagService;

    @Override
    public void onBeforeRemove(@NonNull Set<@NonNull Long> ids) {
        articleTagService.removeArticleTags(ids);
    }
}
