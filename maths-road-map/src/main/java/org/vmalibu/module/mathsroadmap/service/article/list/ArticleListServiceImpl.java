package org.vmalibu.module.mathsroadmap.service.article.list;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleListServiceImpl implements ArticleListService {

    private final DomainObjectPagination<DBArticle, ArticleListElement> domainObjectPagination;

    public ArticleListServiceImpl(ArticleDAO articleDAO, ArticlePageManager articlePageManager) {
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(articleDAO, article -> new ArticleListElement(
                ArticleDTO.from(article),
                articlePageManager.getArticleURI(article.getId()).toString()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull PaginatedDto<ArticleListElement> findAll(@NonNull ArticlePagingRequest pagingRequest) {
        return domainObjectPagination.findAll(
                pagingRequest,
                buildSpecification(
                        pagingRequest.getTitlePrefix(),
                        pagingRequest.getCreatorUsernamePrefix()
                )
        );
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
