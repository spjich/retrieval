package com.dot.fashion.retrieval.core.builder;


import com.dot.fashion.retrieval.core.CallbackRetryLoop;
import com.dot.fashion.retrieval.core.ConditionRetryLoop;
import com.dot.fashion.retrieval.core.RetryConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * title:构造器
 * author:吉
 * since:2018/12/19
 */
@SuppressWarnings("WeakerAccess")
public class RetryBuilder {

    public static final int FOREVER = -1;
    protected RetryConfig retryConfig;
    private static final RetryConfig Default = new RetryConfig(1, -1, Executors.newCachedThreadPool(), 0);


    public RetryBuilder() {
        this.retryConfig = Default;
    }

    public RetryBuilder setConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        return this;
    }


    public RetryConfig getRetryConfig() {
        return retryConfig;
    }


    /**
     * 设置重试次数
     *
     * @param num
     * @return
     */
    public RetryBuilder retry(int num) {
        retryConfig.setRetry(num);
        return this;
    }

    /**
     * 设置线程池
     *
     * @param pool
     * @return
     */
    public RetryBuilder pool(ExecutorService pool) {
        retryConfig.setExecutorService(pool);
        return this;
    }

    /**
     * 重试间隔设置
     *
     * @param mills
     * @return
     */
    public RetryBuilder delay(long mills) {
        retryConfig.setDelayMilli(mills);
        return this;
    }

    /**
     * 设置超时时间
     * 仅在非proceed模式下生效
     *
     * @param millSeconds
     * @return
     */
    public RetryBuilder timeout(long millSeconds) {
        retryConfig.setTimeLimitMilli(millSeconds);
        return this;
    }

    public RetryBuilder failOn(Class<? extends Exception>[] exceptions) {
        retryConfig.setFailOn(exceptions);
        return this;
    }

    public RetryBuilder continueOn(Class<? extends Exception>[] exceptions) {
        retryConfig.setContinueOn(exceptions);
        return this;
    }


    public CallbackRetryLoop build() {
        return new CallbackRetryLoop(retryConfig);
    }

    public ConditionRetryLoop buildCondition() {
        return new ConditionRetryLoop(retryConfig);
    }

}