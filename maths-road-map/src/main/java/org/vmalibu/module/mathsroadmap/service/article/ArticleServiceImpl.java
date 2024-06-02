package org.vmalibu.module.mathsroadmap.service.article;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;
    private final DomainObjectPagination<DBArticle, ArticleDTO> domainObjectPagination;

    @Autowired
    public ArticleServiceImpl(ArticleDAO articleDAO) {
        this.articleDAO = articleDAO;
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(articleDAO, ArticleDTO::from);
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleDTO findArticle(long id) {
        Optional<DBArticle> oTopic = articleDAO.findById(id);
        return oTopic.map(ArticleDTO::from).orElse(null);
    }

    @Override
    public @NonNull PaginatedDto<ArticleDTO> findAll(@NonNull ArticlePagingRequest pagingRequest) {
        return domainObjectPagination.findAll(
                pagingRequest,
                buildSpecification(
                        pagingRequest.getTitlePrefix(),
                        pagingRequest.getCreatorUsernamePrefix()
                )
        );
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO create(@NonNull String title,
                                      @Nullable String description,
                                      @NonNull String latex,
                                      @Nullable String configuration,
                                      @NonNull UserSource userSource) throws PlatformException {
        checkArticle(title, latex);

        DBArticleLatex articleLatex = new DBArticleLatex();
        articleLatex.setLatex(latex);
        articleLatex.setConfiguration(configuration);

        DBArticle article = new DBArticle();
        article.setArticleLatex(articleLatex);
        article.setCreatorUsername(userSource.getUsername());
        article.setDescription(normalizeDescription(description));
        article.setTitle(title);

        articleDAO.save(article);
        return ArticleDTO.from(article);
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

    private static String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.trim() : null;
    }

    private static Specification<DBArticle> buildSpecification(
            String titlePrefix,
            String creatorUsernamePrefix
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (titlePrefix != null) {
                predicates.add(getPrefixPredicate(root, cb, DBArticle.Fields.title, titlePrefix));
            }

            if (creatorUsernamePrefix != null) {
                predicates.add(getPrefixPredicate(root, cb, DBArticle.Fields.creatorUsername, creatorUsernamePrefix));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getPrefixPredicate(
            Root<DBArticle> root,
            CriteriaBuilder cb,
            String fieldName,
            String field
    ) {
        jakarta.persistence.criteria.Path<String> path = root.get(fieldName);
        return cb.like(path, field + "%");
    }

}
