package org.vmalibu.module.mathsroadmap.controller.article;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.article.ArticleService;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleSortField;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.PaginationForm;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_ANON_PREFIX)
@AllArgsConstructor
public class ArticleAnonController {

    private final ArticlePageManager articlePageManager;
    private final ArticleService articleService;

    @GetMapping("/article/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse getArticle(
            @PathVariable("id") long id
    ) throws PlatformException {
        ArticleDTO article = articleService.findArticle(id);
        if (article == null) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticle.class, id);
        }
        URI articleURI = articlePageManager.getArticleURI(article.id());
        return new ArticleResponse(article, articleURI.toString());
    }

    @GetMapping("/article/list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<ArticleDTO> list(
            @RequestParam(required = false) final Map<String, String> params
    ) throws PlatformException {
        ArticlePaginationForm form = new ArticlePaginationForm(params);
        return articleService.findAll(
                new ArticlePagingRequest.Builder(form.page, form.pageSize)
                        .withSort(form.sortField, form.sortDirection)
                        .withTitlePrefix(form.titlePrefix)
                        .withCreatorUsernamePrefix(form.creatorUsernamePrefix)
                        .build()
        );
    }

    public static class ArticlePaginationForm extends PaginationForm {

        static final String JSON_TITLE_PREFIX = "titlePrefix";
        static final String JSON_CREATOR_USERNAME_PREFIX = "creatorUsernamePrefix";

        final ArticleSortField sortField;
        final String titlePrefix;
        final String creatorUsernamePrefix;

        public ArticlePaginationForm(@NonNull Map<String, String> params) throws PlatformException {
            super(params);

            if (params.containsKey(JSON_SORT_FIELD)) {
                this.sortField = parseEnum(ArticleSortField.class, params, JSON_SORT_FIELD);
            } else {
                this.sortField = null;
            }
            this.titlePrefix = params.getOrDefault(JSON_TITLE_PREFIX, null);
            this.creatorUsernamePrefix = params.getOrDefault(JSON_CREATOR_USERNAME_PREFIX, null);
        }

        @Override
        protected int getMaxPageSize() {
            return 1024;
        }
    }

}
