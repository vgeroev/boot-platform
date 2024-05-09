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

    @PostMapping("/article/tex4ht/preview")
    @ResponseStatus(HttpStatus.OK)
    public PreviewArticleResponse previewArticleTex4ht(
            final UserSource userSource,
            @RequestBody TeX4htPreviewArticleRequest previewArticleRequest
    ) throws PlatformException {
        String configuration = StringUtils.hasText(previewArticleRequest.configuration)
                ? previewArticleRequest.configuration
                : null;
        String latex = previewArticleRequest.latex;
        if (!StringUtils.hasText(latex)) {
            throw GeneralExceptionFactory.buildEmptyValueException(TeX4htPreviewArticleRequest.JSON_LATEX);
        }

        String articleURL = articlePageManager.createPreviewPageByTeX4ht(
                latex,
                userSource.getUserId(),
                configuration
        );
        return new PreviewArticleResponse(articleURL);
    }

    @Data
    public static class TeX4htPreviewArticleRequest {

        static final String JSON_CONFIGURATION = "configuration";
        static final String JSON_LATEX = "latex";

        @JsonProperty(JSON_CONFIGURATION)
        private String configuration;

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
