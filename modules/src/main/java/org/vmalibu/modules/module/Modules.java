package org.vmalibu.modules.module;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@PropertySource(
        value = {
                "classpath:boot-properties/cluster.application.properties",
                "classpath:boot-properties/database.application.properties",
                "classpath:boot-properties/web.application.properties"
        }
)
@Slf4j
public class Modules {

    private boolean isInitialized;
    private boolean isDestroyed;
    private List<? extends AbstractModule<?>> modules = new ArrayList<>();

    @Autowired(required = false)
    public Modules(List<? extends AbstractModule<?>> modules) {
        this.modules = modules;
    }

    @PostConstruct
    @Transactional(rollbackFor = Throwable.class)
    public synchronized void init() {
        if (isInitialized) {
            throw new IllegalStateException("Modules are already started.");
        }

        checkAndSortModuleDependencies();

        log.info("Modules are starting...");
        for (AbstractModule<?> module : modules) {
            try {
                module.onStart();
                log.info("Module is started: {}", module.getClass().getCanonicalName());
            } catch (Throwable e) {
                log.error("Exception during module starting: {}", module.getConfig().getUuid(), e);
                System.exit(1);
            }
        }

        isInitialized = true;
        log.info("All modules are started");
    }

    @PreDestroy
    @Transactional
    public synchronized void destroy() {
        if (isDestroyed) {
            throw new IllegalStateException("Modules are already destroyed.");
        }

        log.info("Modules are destroying...");

        for (AbstractModule<?> module : Lists.reverse(modules)) {
            try {
                module.onDestroy();
                log.info("Module is destroyed: {}", module.getClass().getCanonicalName());
            } catch (Throwable e) {
                log.error("Module destroying error: {}", module.getConfig().getUuid(), e);
            }
        }

        isDestroyed = true;
        log.info("All modules are destroyed...");
    }

    private void checkAndSortModuleDependencies() {
        List<AbstractModule<?>> result = new ArrayList<>();
        while (result.size() != modules.size()) {
            AbstractModule<?> next = null;
            for (AbstractModule<?> module : modules) {
                if (!result.contains(module)) {
                    boolean isNext = modules.stream().allMatch(
                            m -> !module.getConfig().getDependencies().contains(m.getClass()) || result.contains(m));
                    if (isNext) {
                        next = module;
                        break;
                    }
                }
            }

            if (next == null) {
                throw new IllegalStateException("Modules have circular dependencies");
            }

            result.add(next);
        }

        modules = Collections.unmodifiableList(result);
    }
}
