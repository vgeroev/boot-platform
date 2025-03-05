package org.vmalibu.module.mathsroadmap.controller.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.service.article.*;
import org.vmalibu.module.mathsroadmap.service.article.pagemanager.ArticlePageManager;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_AUTHORIZED_PREFIX)
@AllArgsConstructor
public class ArticleAuthorizedController {

    private static final int DESCRIPTION_MAX_LENGTH = 1024;

    private final ArticlePageManager articlePageManager;
    private final ArticleService articleService;
    private final ArticleTagService articleTagService;

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

    @PatchMapping("/article/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse updateArticle(
            @PathVariable("id") long id,
            final UserSource userSource,
            @RequestBody UpdateArticleRequest request
    ) throws PlatformException {
        OptionalField<String> title;
        if (request.title.isPresent()) {
            if (!StringUtils.hasText(request.title.get())) {
                throw GeneralExceptionFactory.buildEmptyValueException(UpdateArticleRequest.JSON_TITLE);
            }
            title = OptionalField.of(request.title.get().trim());
        } else {
            title = OptionalField.empty();
        }

        OptionalField<String> description;
        if (request.description.isPresent()) {
            String requestDescription = request.description.get();
            if (StringUtils.hasText(requestDescription)) {
                description = OptionalField.of(requestDescription.trim());
                validateDescription(description.get());
            } else {
                description = OptionalField.of(null);
            }
        } else {
            description = OptionalField.empty();
        }

        ArticleDTO articleDTO = articleService.update(
                id,
                title,
                description,
                userSource
        );

        URI articleURI = articlePageManager.getArticleURI(articleDTO.id());
        return new ArticleResponse(articleDTO, articleURI.toString());
    }

    @GetMapping("/article-latex/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleLatexDTO getArticleLatex(
            @PathVariable("id") long id
    ) throws PlatformException {
        ArticleLatexDTO articleLatex = articleService.findArticleLatex(id);
        if (articleLatex == null) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticleLatex.class, id);
        }
        return articleLatex;
    }

    @PatchMapping("/article-latex/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse updateArticleLatex(
            @PathVariable("id") long id,
            final UserSource userSource,
            @RequestBody UpdateArticleLatexRequest request
    ) throws PlatformException {
        OptionalField<String> latex;
        if (request.latex.isPresent()) {
            if (!StringUtils.hasText(request.latex.get())) {
                throw GeneralExceptionFactory.buildEmptyValueException(UpdateArticleLatexRequest.JSON_LATEX);
            }
            latex = OptionalField.of(request.latex.get().trim());
        } else {
            latex = OptionalField.empty();
        }

        OptionalField<String> configuration;
        if (request.configuration.isPresent()) {
            String requestConfiguration = request.configuration.get();
            if (StringUtils.hasText(requestConfiguration)) {
                configuration = OptionalField.of(requestConfiguration);
            } else {
                configuration = OptionalField.of(null);
            }
        } else {
            configuration = OptionalField.empty();
        }

        ArticleDTO articleDTO = articlePageManager.updateByTeX4ht(
                id,
                latex,
                configuration,
                userSource
        );

        URI articleURI = articlePageManager.getArticleURI(articleDTO.id());
        return new ArticleResponse(articleDTO, articleURI.toString());
    }

    @GetMapping("/article/{id}/like-action")
    @ResponseStatus(HttpStatus.OK)
    public ArticleUserLikeActionResponse getArticleLikeAction(@PathVariable("id") long id,
                                                              final UserSource userSource) {
        ArticleUserLikeAction action = articleService.findArticleLikeAction(id, userSource.getId());
        return new ArticleUserLikeActionResponse(action);
    }

    @PatchMapping("/article/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public ArticleUserLikeActionResponse likeArticle(@PathVariable("id") long id,
                                                     final UserSource userSource) throws PlatformException {
        ArticleUserLikeAction action = articleService.like(id, userSource.getId());
        return new ArticleUserLikeActionResponse(action);
    }

    @PatchMapping("/article/{id}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public ArticleUserLikeActionResponse dislikeArticle(@PathVariable("id") long id,
                                                        final UserSource userSource) throws PlatformException {
        ArticleUserLikeAction action = articleService.dislike(id, userSource.getId());
        return new ArticleUserLikeActionResponse(action);
    }

    @GetMapping("/article/{id}/tags")
    @ResponseStatus(HttpStatus.OK)
    public ArticleTagResponse findTags(@PathVariable("id") long id) {
        return new ArticleTagResponse(articleTagService.findTags(id));
    }

    @PatchMapping("/article/{id}/add-tag/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public void addTag(@PathVariable("id") long id,
                       @PathVariable("tagId") long tagId,
                       final UserSource userSource) throws PlatformException {
        articleTagService.addTag(id, tagId, userSource);
    }

    @PatchMapping("/article/{id}/remove-tag/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTag(@PathVariable("id") long id,
                          @PathVariable("tagId") long tagId,
                          final UserSource userSource) throws PlatformException {
        articleTagService.removeTag(id, tagId, userSource);
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
    public static class UpdateArticleRequest {

        static final String JSON_TITLE = "title";
        static final String JSON_DESCRIPTION = "description";

        private OptionalField<String> title = OptionalField.empty();
        private OptionalField<String> description = OptionalField.empty();

        @JsonProperty(JSON_TITLE)
        public void setTitle(String title) {
            this.title = OptionalField.of(title);
        }

        @JsonProperty(JSON_DESCRIPTION)
        public void setDescription(String description) {
            this.description = OptionalField.of(description);
        }

    }

    @Data
    public static class UpdateArticleLatexRequest {

        static final String JSON_LATEX = "latex";
        static final String JSON_CONFIGURATION = "configuration";

        private OptionalField<String> latex = OptionalField.empty();
        private OptionalField<String> configuration = OptionalField.empty();

        @JsonProperty(JSON_LATEX)
        public void setLatex(String latex) {
            this.latex = OptionalField.of(latex);
        }

        @JsonProperty(JSON_CONFIGURATION)
        public void setConfiguration(String configuration) {
            this.configuration = OptionalField.of(configuration);
        }

    }

    @Data
    @AllArgsConstructor
    public static class PreviewArticleResponse {

        static final String JSON_ARTICLE_URL = "articleURL";

        @JsonProperty(JSON_ARTICLE_URL)
        private String articleURL;
    }

    @Data
    @AllArgsConstructor
    public static class ArticleUserLikeActionResponse {

        static final String JSON_LIKE_ACTION = "like_action";

        @JsonProperty(JSON_LIKE_ACTION)
        private ArticleUserLikeAction action;
    }

}
