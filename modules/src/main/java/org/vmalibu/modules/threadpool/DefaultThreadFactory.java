package org.vmalibu.modules.threadpool;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private final AtomicInteger threadId = new AtomicInteger(1);
    private final String factoryName;

    public DefaultThreadFactory(@NonNull String factoryName) {
        this.factoryName = factoryName;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r, factoryName + "-" + threadId.incrementAndGet());

        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }

        return thread;
    }
}
