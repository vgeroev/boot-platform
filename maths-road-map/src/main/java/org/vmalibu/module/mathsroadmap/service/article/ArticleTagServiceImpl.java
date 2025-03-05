package org.vmalibu.module.mathsroadmap.service.article;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.core.database.dao.TagDAO;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleTagDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleTag;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.*;

@Service
@AllArgsConstructor
public class ArticleTagServiceImpl implements ArticleTagService {

    private final ArticleTagDAO articleTagDAO;
    private final ArticleDAO articleDAO;
    private final TagDAO tagDAO;

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void addTag(long articleId, long tagId, @NonNull UserSource userSource) throws PlatformException {
        if (articleTagDAO.existsById(articleId, tagId)) {
            return;
        }

        Optional<DBArticle> oArticle = articleDAO.findArticlePage(articleId);
        if (oArticle.isEmpty()) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticle.class, articleId);
        }

        if (!tagDAO.existsById(tagId)) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBTag.class, tagId);
        }

        DBArticle article = oArticle.get();
        DBUser creator = article.getCreator();
        validateArticleBelongsToUser(creator, userSource);

        DBTag tag = tagDAO.getReferenceById(tagId);
        DBArticleTag articleTag = new DBArticleTag();
        articleTag.setId(new DBArticleTag.Id(tag, article));

        articleTagDAO.save(articleTag);
        Set<Long> tagIds = new HashSet<>(Set.of(article.getTagIds()));
        tagIds.add(tagId);
        article.setTagIds(tagIds.toArray(new Long[0]));
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void removeTag(long articleId, long tagId, @NonNull UserSource userSource) throws PlatformException {
        Optional<DBArticle> oArticle = articleDAO.findArticlePage(articleId);
        if (oArticle.isEmpty()) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticle.class, articleId);
        }

        DBTag tag = tagDAO.getReferenceById(tagId);
        DBArticle article = oArticle.get();
        DBArticleTag articleTag = articleTagDAO.findById(new DBArticleTag.Id(tag, article)).orElse(null);
        if (articleTag == null) {
            return;
        }

        DBUser creator = article.getCreator();
        validateArticleBelongsToUser(creator, userSource);

        Set<Long> tagIds = new HashSet<>(Set.of(article.getTagIds()));
        tagIds.remove(tagId);
        article.setTagIds(tagIds.toArray(new Long[0]));
        articleDAO.save(article);
        articleTagDAO.delete(articleTag);
    }

    @Override
    @Transactional
    public void removeArticleTags(@NonNull Set<@NonNull Long> tagIds) {
        Set<Long> articleIds = articleTagDAO.deleteAllByTags(tagIds);
        // TODO: Optimize this N+1 problem
        for (Long articleId : articleIds) {
            DBArticle article = articleDAO.findById(articleId).orElse(null);
            if (article != null) {
                Set<Long> articleTagIds = new HashSet<>(Set.of(article.getTagIds()));
                articleTagIds.removeAll(tagIds);
                article.setTagIds(articleTagIds.toArray(new Long[0]));
            }
        }
    }

    @Override
    public @NonNull List<TagDTO> findTags(long articleId) {
        return articleTagDAO.findTagsByArticle(articleId).stream()
                .map(TagDTO::from)
                .toList();
    }

    private void validateArticleBelongsToUser(DBUser creator, UserSource userSource) throws PlatformException {
        if (!Objects.equals(creator.getId(), userSource.getId())) {
            throw MathsRoadMapExceptionFactory.buildUserDoesNotHaveAccessException(userSource.getUsername());
        }
    }
}
