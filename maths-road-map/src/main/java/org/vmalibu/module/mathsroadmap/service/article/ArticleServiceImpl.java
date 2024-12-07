package org.vmalibu.module.mathsroadmap.service.article;

import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleLatexDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListElement;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.vmalibu.modules.utils.database.DatabaseFunctionNames.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;
    private final ArticleLatexDAO articleLatexDAO;
    private final UserDAO userDAO;
    private final DomainObjectPagination<DBArticle, ArticleListElement> domainObjectPagination;

    @Autowired
    public ArticleServiceImpl(ArticleDAO articleDAO, ArticleLatexDAO articleLatexDAO, UserDAO userDAO) {
        this.articleDAO = articleDAO;
        this.articleLatexDAO = articleLatexDAO;
        this.userDAO = userDAO;
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(articleDAO, ArticleListElement::from);
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleDTO findArticle(long id) {
        Optional<DBArticle> oArticle = articleDAO.findById(id);
        return oArticle.map(ArticleDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull PaginatedDto<ArticleListElement> findAll(@NonNull ArticlePagingRequest pagingRequest) {
        return domainObjectPagination.findAll(
                pagingRequest,
                buildSpecification(
                        pagingRequest.getSearchText()
                )
        );
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

        if (latex.isPresent() && configuration.isPresent()) {
            String newLatex = latex.get();
            checkLatex(newLatex);
            articleLatexDAO.updateLatex(id, newLatex, configuration.get());
        } else if (latex.isPresent()) {
            String newLatex = latex.get();
            checkLatex(newLatex);
            articleLatexDAO.updateLatex(id, newLatex);
        } else if (configuration.isPresent()) {
            articleLatexDAO.updateConfiguration(id, configuration.get());
        }

        return ArticleDTO.from(article);
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
