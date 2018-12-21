package com.dot.fashion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class RetryBuilder {
    public static final int FOREVER = -1;
    private RetryConfig retryConfig;
    private static final RetryConfig Default = new RetryConfig(1, -1, Executors.newCachedThreadPool());


    public RetryBuilder() {
        this.retryConfig = Default;
    }

    public RetryBuilder setConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        return this;
    }

    public RetryBuilder num(int num) {
        retryConfig.setNum(num);
        return this;
    }

    public RetryBuilder pool(ExecutorService pool) {
        retryConfig.setExecutorService(pool);
        return this;
    }

    public RetryBuilder timeout(long millSeconds) {
        retryConfig.setTimeLimitMilli(millSeconds);
        return this;
    }

    protected RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public RetryLoop build() {
        return new RetryLoop(retryConfig);
    }
}
