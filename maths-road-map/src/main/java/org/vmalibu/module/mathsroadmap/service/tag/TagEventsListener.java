package org.vmalibu.module.mathsroadmap.service.tag;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.vmalibu.module.core.service.tag.event.BeforeTagsRemoveEvent;
import org.vmalibu.module.mathsroadmap.service.article.ArticleTagService;

import java.util.Set;

@Service
@AllArgsConstructor
public class TagEventsListener {

    private final ArticleTagService articleTagService;

    @EventListener
    public void onBeforeRemoveEvent(@NonNull BeforeTagsRemoveEvent event) {
        Set<@NonNull Long> ids = event.ids();
        if (!ids.isEmpty()) {
            articleTagService.removeArticleTags(ids);
        }
    }
}
