package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import com.antkorwin.xsync.XSync;
import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.article.ArticleLatexDTO;
import org.vmalibu.module.mathsroadmap.service.article.ArticleService;
import org.vmalibu.module.mathsroadmap.service.config.MathsRoadMapConfigService;
import org.vmalibu.module.mathsroadmap.service.config.NginxArticlesConfig;
import org.vmalibu.module.mathsroadmap.service.latexconverter.tex4ht.TeX4htLatexConverter;
import org.vmalibu.module.mathsroadmap.utils.ArticleURIUtils;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;
import org.vmalibu.modules.utils.function.Supplier;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class ArticlePageManagerImpl implements ArticlePageManager {

    private final ArticleService articleService;
    private final TeX4htLatexConverter tex4htLatexConverter;
    private final MathsRoadMapConfigService mathsRoadMapConfigService;
    private final XSync<String> articlePreviewXSync;

    public ArticlePageManagerImpl(ArticleService articleService,
                                  TeX4htLatexConverter tex4htLatexConverter,
                                  MathsRoadMapConfigService mathsRoadMapConfigService,
                                  @Qualifier("articlePreviewXSync") XSync<String> articlePreviewXSync) {
        this.articleService = articleService;
        this.tex4htLatexConverter = tex4htLatexConverter;
        this.mathsRoadMapConfigService = mathsRoadMapConfigService;
        this.articlePreviewXSync = articlePreviewXSync;
    }

    @Override
    public @NonNull URI createPreviewByTeX4ht(@NonNull String latex,
                                              @Nullable String configuration,
                                              @NonNull UserSource userSource) {
        String userId = userSource.getUserId();
        String dirname = getPreviewDirname(userId);
        articlePreviewXSync.execute(userId, () -> internalCreatePreviewPage(latex, configuration, dirname));
        return ArticleURIUtils.getArticlePreviewURI(getNginxURI(), dirname);
    }

    @Override
    public @NonNull ArticleDTO createByTeX4ht(@NonNull String title,
                                              @Nullable String description,
                                              @NonNull String latex,
                                              @Nullable String configuration,
                                              @NonNull UserSource userSource) throws PlatformException {
        Supplier<ArticleDTO> articleGetter =
                () -> articleService.create(title, description, latex, configuration, userSource);
        return createArticlePage(articleGetter, latex, configuration, userSource);
    }

    @Override
    public @NonNull ArticleDTO updateByTeX4ht(long id,
                                              @NonNull OptionalField<@NonNull String> latex,
                                              @NonNull OptionalField<@Nullable String> configuration,
                                              @NonNull UserSource userSource) {
        String userId = userSource.getUserId();
        return articlePreviewXSync.evaluate(userId, () -> internalUpdate(id, latex, configuration, userSource));
    }

    @SneakyThrows
    private ArticleDTO internalUpdate(long id,
                                      OptionalField<String> latex,
                                      OptionalField<String> configuration,
                                      UserSource userSource) {
        String nLatex;
        String nConfiguration;
        if (latex.isPresent() && configuration.isPresent()) {
            nLatex = latex.get();
            nConfiguration = configuration.get();
        } else {
            ArticleLatexDTO articleLatex = articleService.findArticleLatex(id);
            if (articleLatex == null) {
                throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticleLatex.class, id);
            }

            if (latex.isPresent()) {
                nLatex = latex.get();
            } else {
                nLatex = articleLatex.latex();
            }

            if (configuration.isPresent()) {
                nConfiguration = configuration.get();
            } else {
                nConfiguration = articleLatex.configuration();
            }
        }

        Supplier<ArticleDTO> articleGetter = () -> articleService.updateLatex(id, latex, configuration, userSource);
        return createArticlePage(
                articleGetter,
                nLatex,
                nConfiguration,
                userSource
        );
    }

    private ArticleDTO createArticlePage(Supplier<ArticleDTO> articleGetter,
                                         String latex,
                                         String configuration,
                                         UserSource userSource) throws PlatformException {
        String userId = userSource.getUserId();
        Path candidatePath = getCandidatePagePath(userId);
        try {
            createPage(latex, configuration, candidatePath);
            ArticleDTO articleDTO = articleGetter.get();
            submitCandidate(articleDTO.id(), candidatePath);
            return articleDTO;
        } finally {
            try {
                if (Files.exists(candidatePath)) {
                    FileSystemUtils.deleteRecursively(candidatePath);
                }
            } catch (IOException e) {
                log.error("Failed to delete candidate directory", e);
            }
        }
    }

    @Override
    public @NonNull URI getArticleURI(long articleId) {
        return ArticleURIUtils.getArticleURI(getNginxURI(), articleId);
    }

    private URI getNginxURI() {
        try {
            return mathsRoadMapConfigService.getNginxConfig().url().toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @SneakyThrows
    private void internalCreatePreviewPage(String latex,
                                           String configuration,
                                           String dirname) {
        Path dir = mathsRoadMapConfigService.getNginxConfig().nginxArticlesConfig().previewDir();
        Path pagePath = dir.resolve(dirname);
        createPage(latex, configuration, pagePath);
    }

    private void createPage(String latex,
                            String configuration,
                            Path pagePath) throws PlatformException {
        try {
            FileSystemUtils.deleteRecursively(pagePath);
        } catch (IOException e) {
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
        tex4htLatexConverter.toHtml(latex, pagePath, configuration);
    }

    private static String getPreviewDirname(String userId) {
        return hashUserId(userId);
    }

    private static Path getCandidatePagePath(String userId) throws PlatformException {
        UUID uuid = UUID.randomUUID();
        try {
            return Files.createTempDirectory("article-candidate-" + hashUserId(userId) + "-" + uuid);
        } catch (IOException e) {
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private void submitCandidate(long articleId, Path candidatePath) throws PlatformException {
        NginxArticlesConfig nginxArticlesConfig = mathsRoadMapConfigService.getNginxConfig().nginxArticlesConfig();
        Path pagePath = nginxArticlesConfig.dir().resolve(String.valueOf(articleId));
        try {
            FileSystemUtils.copyRecursively(candidatePath, pagePath);
        } catch (IOException e) {
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private static String hashUserId(String userId) {
        return Hashing.sha256()
                .hashString(userId, StandardCharsets.UTF_8)
                .toString();
    }

}