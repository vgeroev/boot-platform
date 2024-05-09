package org.vmalibu.module.mathsroadmap.service.article.pagemanager;

import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.vmalibu.module.mathsroadmap.service.config.MathsRoadMapConfigService;
import org.vmalibu.module.mathsroadmap.service.config.NginxConfig;
import org.vmalibu.module.mathsroadmap.service.latexconverter.tex4ht.TeX4htLatexConverter;
import org.vmalibu.module.mathsroadmap.service.nginx.NginxControl;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@AllArgsConstructor
public class ArticlePageManagerImpl implements ArticlePageManager {

    private final TeX4htLatexConverter tex4htLatexConverter;
    private final NginxControl nginxControl;
    private final MathsRoadMapConfigService mathsRoadMapConfigService;

    @Override
    public @NonNull String createPreviewPageByTeX4ht(@NonNull String latex,
                                                     @NonNull String userId,
                                                     @Nullable String configuration) throws PlatformException {
        String dirname = getPreviewDirname(userId);
        internalCreatePreviewPage(latex, configuration, dirname);
        createPreviewConf(dirname);
        nginxControl.reload();
        return getPreviewPageURL(dirname);
    }

    private String getPreviewPageURL(String dirname) {
        NginxConfig nginxConfig = mathsRoadMapConfigService.getNginxConfig();
        try {
            return nginxConfig.url().toURI().resolve("/articles/preview/%s/".formatted(dirname)).toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private void internalCreatePreviewPage(String latex,
                                           String configuration,
                                           String dirname) throws PlatformException {
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

    private void createPreviewConf(String dirname) throws PlatformException {
        Path confDir = mathsRoadMapConfigService.getNginxConfig().nginxArticlesConfig().previewConfDir();
        createConf(NginxLocationTemplates.PREVIEW, confDir, dirname);
    }

    private void createConf(String confTemplate, Path confDir, String dirname) throws PlatformException {
        try {
            Path confPath = confDir.resolve(dirname + ".conf");
            if (!Files.exists(confPath)) {
                String conf = confTemplate.formatted(dirname);
                Files.writeString(confPath, conf);
            }
        } catch (IOException e) {
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private static String getPreviewDirname(String userId) {
        return hashUserId(userId);
    }

    private static String hashUserId(String userId) {
        return Hashing.sha256()
                .hashString(userId, StandardCharsets.UTF_8)
                .toString();
    }

    private static class NginxLocationTemplates {

        private static final String PREVIEW =
                """
                location /articles/preview/%s/ {
                    index index.html;
                }
                """;
    }
}