package org.vmalibu.module.mathsroadmap.service.article;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.List;
import java.util.Set;

public interface ArticleTagService {

    void addTag(long articleId, long tagId, @NonNull UserSource userSource) throws PlatformException;

    void removeTag(long articleId, long tagId, @NonNull UserSource userSource) throws PlatformException;

    void removeArticleTags(@NonNull Set<@NonNull Long> tagIds);

    @NonNull List<TagDTO> findTags(long articleId);
}
