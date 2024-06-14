package org.vmalibu.module.mathsroadmap.service.config;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

@Service
@Slf4j
public class MathsRoadMapConfigServiceImpl implements MathsRoadMapConfigService {

    private final NginxConfig nginxConfig;

    public MathsRoadMapConfigServiceImpl(@Value("${maths-road-map.nginx.scheme}") String scheme,
                                         @Value("${maths-road-map.nginx.host}") String host,
                                         @Value("${maths-road-map.nginx.port}") int port,
                                         @Value("${maths-road-map.nginx.articles.preview.dir}") Path articlesPreviewDir,
                                         @Value("${maths-road-map.nginx.articles.dir}") Path articlesDir) {
        this.nginxConfig = buildNginxConfig(scheme, host, port, articlesPreviewDir, articlesDir);
        log.info("Nginx config: {}", nginxConfig);
    }

    @Override
    public @NonNull NginxConfig getNginxConfig() {
        return nginxConfig;
    }

    private NginxConfig buildNginxConfig(String scheme,
                                         String host,
                                         int port,
                                         Path articlesPreviewDir,
                                         Path articlesDir) {
        NginxArticlesConfig nginxArticlesConfig = new NginxArticlesConfig(
                articlesPreviewDir.toAbsolutePath(),
                articlesDir.toAbsolutePath()
        );

        URL url;
        try {
            url = new URL(scheme, host, getURLPort(port), "");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        return new NginxConfig(url, nginxArticlesConfig);
    }

    private int getURLPort(int port) {
        return switch (port) {
            case 80, 443 -> -1;
            default -> port;
        };
    }
}
