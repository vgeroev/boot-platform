package org.vmalibu.module.mathsroadmap.controller.article;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.article.ArticleService;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.net.URI;

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

}
