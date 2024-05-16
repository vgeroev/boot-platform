package org.vmalibu.module.mathsroadmap.service.article;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleTreeDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;
    private final ArticleTreeDAO articleTreeDAO;

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleDTO findArticle(long id) {
        Optional<DBArticle> oTopic = articleDAO.findById(id);
        return oTopic.map(ArticleDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO create(@NonNull String title,
                                      @NonNull String latex,
                                      @Nullable String configuration,
                                      @NonNull AbstractionLevel abstractionLevel,
                                      @NonNull Set<Long> prevNodeIds,
                                      @NonNull Set<Long> nextNodeIds,
                                      @NonNull UserSource userSource) throws PlatformException {
        checkArticle(title, latex);

        Set<DBArticle> prevArticles = getArticles(prevNodeIds);
        Set<DBArticle> nextArticles = getArticles(nextNodeIds);

        checkNode(prevArticles, nextArticles);

        DBArticleLatex articleLatex = new DBArticleLatex();
        articleLatex.setLatex(latex);
        articleLatex.setConfiguration(configuration);

        DBArticle article = new DBArticle();
        article.setArticleLatex(articleLatex);
        article.setCreatorUsername(userSource.getUsername());
        article.setAbstractionLevel(abstractionLevel);
        article.setTitle(title);
        article.setPrevArticles(prevArticles);
        article.setNextArticles(nextArticles);

        articleDAO.save(article);
        return ArticleDTO.from(article);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void addNodes(long id,
                         @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                         @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException {
        DBArticle articleNode = articleDAO.checkExistenceAndGet(id);
        if (prevNodeIds.isPresent() || nextNodeIds.isPresent()) {
            Set<DBArticle> prevNodes = articleNode.getPrevArticles();
            Set<DBArticle> nextNodes = articleNode.getNextArticles();

            if (prevNodeIds.isPresent()) {
                prevNodes.addAll(getArticles(prevNodeIds.get()));
            }

            if (nextNodeIds.isPresent()) {
                nextNodes.addAll(getArticles(nextNodeIds.get()));
            }

            checkNode(prevNodes, nextNodes);
        }
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void updateNode(long id,
                           @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                           @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException {
        DBArticle article = articleDAO.checkExistenceAndGet(id);

        Set<DBArticle> prevNodes;
        if (prevNodeIds.isPresent()) {
            prevNodes = getArticles(prevNodeIds.get());
        } else {
            prevNodes = article.getPrevArticles();
        }

        Set<DBArticle> nextNodes;
        if (nextNodeIds.isPresent()) {
            nextNodes = getArticles(nextNodeIds.get());
        } else {
            nextNodes = article.getNextArticles();
        }

        checkNode(prevNodes, nextNodes);

        article.setPrevArticles(prevNodes);
        article.setNextArticles(nextNodes);
    }

    private Set<DBArticle> getArticles(Set<Long> ids) {
        List<Long> nonnullIds = removeNulls(ids);
        if (nonnullIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(articleDAO.findAllById(nonnullIds));
    }

    private static void checkArticle(String title,
                                     String latex) throws PlatformException {
        checkTitle(title);
        checkLatex(latex);
    }

    private static void checkTitle(String title) throws PlatformException {
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBArticleLatex.class, DBArticle.Fields.title);
        }
    }

    private static void checkLatex(String body) throws PlatformException {
        if (!StringUtils.hasText(body)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBArticleLatex.class, DBArticleLatex.Fields.latex);
        }
    }

    private void checkNode(Set<DBArticle> prevNodes,
                                  Set<DBArticle> nextNodes) throws PlatformException {
        checkCycles(prevNodes, nextNodes);
    }

    private void checkCycles(Set<DBArticle> prevArticles, Set<DBArticle> nextArticles) throws PlatformException {
        if (prevArticles.isEmpty() || nextArticles.isEmpty()) {
            return;
        }

        Set<Long> prevIds = prevArticles.stream()
                .map(DomainObject::getId)
                .collect(Collectors.toSet());

        Set<Long> nextIds = nextArticles.stream()
                .map(DomainObject::getId)
                .collect(Collectors.toSet());

        while (!nextIds.isEmpty()) {
            for (Long nextId : nextIds) {
                if (prevIds.contains(nextId)) {
                    throw MathsRoadMapExceptionFactory.buildArticlesHaveCycleException();
                }
            }

            nextIds = articleTreeDAO.getNextArticles(nextIds)
                    .stream()
                    .map(t -> t.getId().getFkNext())
                    .collect(Collectors.toSet());
        }
    }

    private static <T> List<T> removeNulls(Collection<T> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .toList();
    }

}
