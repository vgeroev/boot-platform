package org.vmalibu.module.mathsroadmap.controller.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_AUTHORIZED_PREFIX)
@AllArgsConstructor
public class ArticleController {

    private final ArticlePageManager articlePageManager;

    @PostMapping("/article/preview")
    @ResponseStatus(HttpStatus.OK)
    public PreviewArticleResponse previewArticle(
            final UserSource userSource,
            @RequestBody PreviewArticleRequest previewArticleRequest
    ) throws PlatformException {
        String latex = previewArticleRequest.latex;
        if (!StringUtils.hasText(latex)) {
            throw GeneralExceptionFactory.buildEmptyValueException(PreviewArticleRequest.JSON_LATEX);
        }

        String articleURL = articlePageManager.createPreviewPage(latex, userSource.getUserId());
        return new PreviewArticleResponse(articleURL);
    }

    @Data
    public static class PreviewArticleRequest {

        static final String JSON_LATEX = "latex";

        @JsonProperty(JSON_LATEX)
        private String latex;
    }

    @Data
    @AllArgsConstructor
    public static class PreviewArticleResponse {

        static final String JSON_ARTICLE_URL = "article_url";

        @JsonProperty(JSON_ARTICLE_URL)
        private String articleURL;
    }

}
