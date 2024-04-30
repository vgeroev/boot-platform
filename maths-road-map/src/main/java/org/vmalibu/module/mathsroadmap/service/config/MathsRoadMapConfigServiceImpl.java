package org.vmalibu.module.mathsroadmap.service.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

@Service
public class MathsRoadMapConfigServiceImpl implements MathsRoadMapConfigService {

    private final NginxConfig nginxConfig;

    public MathsRoadMapConfigServiceImpl(@Value("${maths-road-map.nginx.scheme}") String scheme,
                                         @Value("${maths-road-map.nginx.host}") String host,
                                         @Value("${maths-road-map.nginx.port}") int port,
                                         @Value("${maths-road-map.nginx.reload-cmd}") String reloadCmd,
                                         @Value("${maths-road-map.nginx.articles.preview.conf-dir}") Path previewConfDir,
                                         @Value("${maths-road-map.nginx.articles.preview.dir}") Path previewDir) {
        this.nginxConfig = buildNginxConfig(scheme, host, port, reloadCmd, previewConfDir, previewDir);
    }

    @Override
    public @NonNull NginxConfig getNginxConfig() {
        return nginxConfig;
    }

    private NginxConfig buildNginxConfig(String scheme,
                                         String host,
                                         int port,
                                         String reloadCmd,
                                         Path previewConfDir,
                                         Path previewDir) {
        NginxArticlesConfig nginxArticlesConfig = new NginxArticlesConfig(
                previewConfDir.toAbsolutePath(),
                previewDir.toAbsolutePath()
        );

        URL url;
        try {
            url = new URL(scheme, host, port, "");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        return new NginxConfig(url, reloadCmd, nginxArticlesConfig);
    }
}
