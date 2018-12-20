package com.dot.fashion;

import java.util.concurrent.Executors;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class RetryBuilder {
    private RetryConfig retryConfig;
    private static final RetryConfig Default = new RetryConfig(1, -1, Executors.newCachedThreadPool());


    public RetryBuilder() {
        this.retryConfig = Default;
    }

    public RetryBuilder setConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        return this;
    }

    public RetryLoop build() {
        return new RetryLoop(retryConfig);
    }
}
