package org.vmalibu.modules.service.platformmanager;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.modules.module.AbstractModule;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PlatformManagerImpl implements PlatformManager {

    private boolean isInitialized;
    private boolean isDestroyed;
    private List<? extends AbstractModule<?>> modules;

    @Autowired(required = false)
    public PlatformManagerImpl(List<? extends AbstractModule<?>> modules) {
        this.modules = modules;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void start() throws PlatformException {
        synchronized (PlatformManagerImpl.class) {
            if (isInitialized) {
                throw new IllegalStateException("Platform is already started.");
            }

            checkAndSortModuleDependencies();

            log.info("Platform is starting...");

            for (AbstractModule<?> module : modules) {
                try {
                    module.onStart();
                    log.info("Module is started: {}", module.getClass().getCanonicalName());
                } catch (Throwable e) {
                    log.error("Exception during module starting: {}", module.getConfig().getUuid(), e);
//                System.exit(1);
                    throw e;
                }
            }

            isInitialized = true;
            log.info("Platform is started.");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void destroy() {
        synchronized (PlatformManagerImpl.class) {
            if (isDestroyed) {
                throw new IllegalStateException("Platform is already destroyed.");
            }

            log.info("Platform is destroying...");

            for (AbstractModule<?> module : Lists.reverse(modules)) {
                try {
                    module.onDestroy();
                    log.info("Module is destroyed: {}", module.getClass().getCanonicalName());
                } catch (Throwable e) {
                    log.error("Module destroying error: {}", module.getConfig().getUuid(), e);
                }
            }

            isDestroyed = true;
            log.info("Platform is destroyed.");
        }
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
