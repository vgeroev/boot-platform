package org.vmalibu.module.mathsroadmap.service.article;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.core.service.tag.TagService;
import org.vmalibu.module.core.service.tag.list.TagPagingRequest;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleLatexDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleUserLikesDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleUserLikes;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListElement;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListTags;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagination;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.*;

import static org.vmalibu.modules.utils.database.DatabaseFunctionNames.*;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;
    private final ArticleLatexDAO articleLatexDAO;
    private final UserDAO userDAO;
    private final ArticleUserLikesDAO articleUserLikesDAO;
    private final TagService tagService;

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleDTO findArticle(long id) {
        Optional<DBArticle> oArticle = articleDAO.findById(id);
        return oArticle.map(ArticleDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticlePageDTO findArticlePage(long id) {
        Optional<DBArticle> oArticle = articleDAO.findArticlePage(id);
        return oArticle.map(ArticlePageDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull ArticleListTags findAll(@NonNull ArticlePagingRequest pagingRequest) {
        PaginatedDto<ArticleListElement> articles = new ArticlePagination(articleDAO).findAll(
                pagingRequest,
                buildSpecification(
                        pagingRequest.getSearchText()
                )
        );

        Set<Long> tags = new HashSet<>();
        for (ArticleListElement element : articles.getResult()) {
            tags.addAll(element.tagIds());
        }

        PaginatedDto<TagDTO> tagList = tagService.findAll(
                new TagPagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withFilterIds(tags)
                        .build()
        );

        return new ArticleListTags(articles, tagList.getResult());
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleLatexDTO findArticleLatex(long id) {
        Optional<DBArticleLatex> oArticleLatex = articleLatexDAO.findById(id);
        return oArticleLatex.map(ArticleLatexDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO create(@NonNull String title,
                                      @Nullable String description,
                                      @NonNull String latex,
                                      @Nullable String configuration,
                                      @NonNull UserSource userSource) throws PlatformException {
        checkArticle(title, latex);
        DBUser user = userDAO.checkExistenceAndGet(userSource.getId());

        DBArticleLatex articleLatex = new DBArticleLatex();
        articleLatex.setLatex(latex);
        articleLatex.setConfiguration(configuration);

        DBArticle article = new DBArticle();
        article.setArticleLatex(articleLatex);
        article.setCreator(user);
        article.setDescription(normalizeDescription(description));
        article.setTitle(title);
        article.setLikes(0);
        article.setDislikes(0);

        articleDAO.save(article);
        return ArticleDTO.from(article);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO update(long id,
                                      @NonNull OptionalField<@NonNull String> title,
                                      @NonNull OptionalField<@Nullable String> description,
                                      @NonNull UserSource userSource) throws PlatformException {
        DBArticle article = articleDAO.checkExistenceAndGet(id);
        validateArticleBelongsToUser(article, userSource);

        if (title.isPresent()) {
            String newTitle = title.get();
            checkTitle(newTitle);
            article.setTitle(newTitle);
        }

        if (description.isPresent()) {
            article.setDescription(normalizeDescription(description.get()));
        }

        return ArticleDTO.from(articleDAO.save(article));
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO updateLatex(long id,
                                           @NonNull OptionalField<@NonNull String> latex,
                                           @NonNull OptionalField<@Nullable String> configuration,
                                           @NonNull UserSource userSource) throws PlatformException {
        DBArticle article = articleDAO.checkExistenceAndGet(id);
        validateArticleBelongsToUser(article, userSource);

        // Updating version of DBArticle when DBArticleLatex is updated
        if (latex.isPresent() && configuration.isPresent()) {
            article.setVersion((short) (article.getVersion() + 1));
            String newLatex = latex.get();
            checkLatex(newLatex);
            articleLatexDAO.updateLatex(id, newLatex, configuration.get());
        } else if (latex.isPresent()) {
            article.setVersion((short) (article.getVersion() + 1));
            String newLatex = latex.get();
            checkLatex(newLatex);
            articleLatexDAO.updateLatex(id, newLatex);
        } else if (configuration.isPresent()) {
            article.setVersion((short) (article.getVersion() + 1));
            articleLatexDAO.updateConfiguration(id, configuration.get());
        }

        return ArticleDTO.from(article);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleUserLikeAction like(long id, long userId) throws PlatformException {
        return setLikeValue(id, userId, true);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleUserLikeAction dislike(long id, long userId) throws PlatformException {
        return setLikeValue(id, userId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull ArticleUserLikeAction findArticleLikeAction(long id, long userId) {
        Optional<DBArticleUserLikes> oLikeAction = articleUserLikesDAO.findByArticleAndUser(id, userId);
        if (oLikeAction.isEmpty()) {
            return ArticleUserLikeAction.NO_ACTION;
        }
        DBArticleUserLikes articleUserLikes = oLikeAction.get();
        return articleUserLikes.isLike() ? ArticleUserLikeAction.LIKED : ArticleUserLikeAction.DISLIKED;
    }

    private ArticleUserLikeAction setLikeValue(long id, long userId, boolean like) throws PlatformException {
        if (!userDAO.existsById(userId)) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBUser.class, userId);
        }

        Optional<Long> articleIdHolder = articleDAO.lockOnPessimisticWrite(id);
        if (articleIdHolder.isEmpty()) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticle.class, id);
        }

        DBArticleUserLikes.Id artileUserLikeId = DBArticleUserLikes.Id.builder()
                .article(articleDAO.getReferenceById(id))
                .user(userDAO.getReferenceById(userId))
                .build();

        Optional<DBArticleUserLikes> oArticleUserLikes = articleUserLikesDAO.findById(artileUserLikeId);
        if (oArticleUserLikes.isPresent()) {
            DBArticleUserLikes articleUserLikes = oArticleUserLikes.get();
            return modifyExistingLikeAction(id, articleUserLikes, like);
        } else {
            DBArticleUserLikes articleUserLikes = new DBArticleUserLikes();
            articleUserLikes.setId(artileUserLikeId);
            articleUserLikes.setValue(like);
            articleUserLikesDAO.save(articleUserLikes);
            if (like) {
                articleDAO.incrementLikes(id);
                return ArticleUserLikeAction.LIKED;
            } else {
                articleDAO.incrementDislikes(id);
                return ArticleUserLikeAction.DISLIKED;
            }
        }
    }

    private ArticleUserLikeAction modifyExistingLikeAction(long id, DBArticleUserLikes articleUserLikes, boolean like) {
        if (articleUserLikes.getValue() == like) {
            articleUserLikesDAO.delete(articleUserLikes);
            if (like) {
                articleDAO.decrementLikes(id);
            } else {
                articleDAO.decrementDislikes(id);
            }
            return ArticleUserLikeAction.NO_ACTION;
        } else {
            articleUserLikes.setValue(like);
            if (like) {
                articleDAO.swapDislikeOnLike(id);
                return ArticleUserLikeAction.LIKED;
            } else {
                articleDAO.swapLikeOnDislike(id);
                return ArticleUserLikeAction.DISLIKED;
            }
        }
    }

    private void validateArticleBelongsToUser(DBArticle article, UserSource userSource) throws PlatformException {
        if (!Objects.equals(article.getCreator().getId(), userSource.getId())) {
            throw MathsRoadMapExceptionFactory.buildUserDoesNotHaveAccessException(userSource.getUsername());
        }
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

    private static void checkLatex(String latex) throws PlatformException {
        if (!StringUtils.hasText(latex)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBArticleLatex.class, DBArticleLatex.Fields.latex);
        }
    }

    private static String normalizeDescription(String description) {
        // I'am using empty string instead of null because ('abc' || null) = null in PostgresSQL
        return StringUtils.hasText(description) ? description.trim() : "";
    }

    private static Specification<DBArticle> buildSpecification(
            String searchText
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            root.fetch(DBArticle.Fields.creator, JoinType.LEFT);

            if (searchText != null) {
                predicates.add(getSearchTextPredicate(root, cb, searchText));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getSearchTextPredicate(
            Root<DBArticle> root,
            CriteriaBuilder cb,
            String value
    ) {
        Path<String> title = root.get(DBArticle.DB_TITLE);
        Path<String> description = root.get(DBArticle.DB_DESCRIPTION);
        Expression<String> concat = cb.function(CONCAT_TRIPLE, String.class, title, cb.literal(' '), description);
        Expression<Boolean> trgm = cb.function(PG_TRGM_CONTAINED_BY, Boolean.class, cb.literal(value), concat);
        return cb.equal(trgm, true);
    }

}
