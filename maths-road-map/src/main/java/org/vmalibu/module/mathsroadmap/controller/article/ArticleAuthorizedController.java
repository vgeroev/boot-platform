package org.vmalibu.module.mathsroadmap.controller.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.net.URI;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_AUTHORIZED_PREFIX)
@AllArgsConstructor
public class ArticleAuthorizedController {

    private static final int DESCRIPTION_MAX_LENGTH = 1024;

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

        URI articleURI = articlePageManager.createPreviewByTeX4ht(
                latex,
                configuration,
                userSource
        );
        return new PreviewArticleResponse(articleURI.toString());
    }

    @PostMapping("/article")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponse createArticle(
            final UserSource userSource,
            @RequestBody CreateArticleRequest request
    ) throws PlatformException {
        if (!StringUtils.hasText(request.title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(CreateArticleRequest.JSON_TITLE);
        }
        String title = request.title.trim();

        String description = request.description;
        validateDescription(description);

        if (!StringUtils.hasText(request.latex)) {
            throw GeneralExceptionFactory.buildEmptyValueException(TeX4htPreviewArticleRequest.JSON_LATEX);
        }
        String latex = request.latex;

        String configuration = StringUtils.hasText(request.configuration) ? request.configuration : null;

        ArticleDTO articleDTO = articlePageManager.createByTeX4ht(
                title,
                description,
                latex,
                configuration,
                userSource
        );

        URI articleURI = articlePageManager.getArticleURI(articleDTO.id());
        return new ArticleResponse(articleDTO, articleURI.toString());
    }

    private void validateDescription(String description) throws PlatformException {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            throw GeneralExceptionFactory.buildInvalidArgumentException(
                    "Description exceeded max length - " + DESCRIPTION_MAX_LENGTH);
        }
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
    public static class CreateArticleRequest {

        static final String JSON_TITLE = "title";
        static final String JSON_DESCRIPTION = "description";
        static final String JSON_LATEX = "latex";
        static final String JSON_CONFIGURATION = "configuration";

        @JsonProperty(JSON_TITLE)
        private String title;

        @JsonProperty(JSON_DESCRIPTION)
        private String description;

        @JsonProperty(JSON_LATEX)
        private String latex;

        @JsonProperty(JSON_CONFIGURATION)
        private String configuration;

    }

    @Data
    @AllArgsConstructor
    public static class PreviewArticleResponse {

        static final String JSON_ARTICLE_URL = "articleURL";

        @JsonProperty(JSON_ARTICLE_URL)
        private String articleURL;
    }

}
