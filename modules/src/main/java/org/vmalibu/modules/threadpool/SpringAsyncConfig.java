package org.vmalibu.modules.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@Slf4j
public class SpringAsyncConfig implements AsyncConfigurer {

    private final DefaultThreadPoolExecutor executor;

    public SpringAsyncConfig() {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        this.executor = new DefaultThreadPoolExecutor(
                nProcessors,
                2 * nProcessors,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new DefaultThreadFactory("spring-async-thread-factory"));
    }

    @Bean("defaultThreadPoolTaskExecutor")
    Executor getDefaultThreadPoolExecutor() {
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("Exception in thread pool executor method: {}, params: {}", method, params, ex);
    }
}
