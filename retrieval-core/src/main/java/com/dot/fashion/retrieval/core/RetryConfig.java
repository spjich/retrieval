package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.builder.RetryBuilder;

import java.util.concurrent.ExecutorService;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class RetryConfig {

    //重试次数
    private Integer retry;

    //整个重试等待时间
    private long timeLimitMilli;

    //异步执行线程池
    private ExecutorService executorService;

    //每次重试等待时间
    private long delayMilli;
    //失败跳出
    private Class<? extends Exception>[] failOn;
    //失败继续
    private Class<? extends Exception>[] continueOn;

    private RetryBuilder.TimeoutPolice timeoutPolice;


    Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public long getTimeLimitMilli() {
        return timeLimitMilli;
    }

    public void setTimeLimitMilli(long timeLimitMilli) {
        this.timeLimitMilli = timeLimitMilli;
    }


    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public long getDelayMilli() {
        return delayMilli;
    }

    public void setDelayMilli(long delayMilli) {
        this.delayMilli = delayMilli;
    }

    public Class<? extends Exception>[] getFailOn() {
        return failOn;
    }

    public void setFailOn(Class<? extends Exception>[] failOn) {
        this.failOn = failOn;
    }

    public Class<? extends Exception>[] getContinueOn() {
        return continueOn;
    }

    public void setContinueOn(Class<? extends Exception>[] continueOn) {
        this.continueOn = continueOn;
    }

    public RetryBuilder.TimeoutPolice getTimeoutPolice() {
        return timeoutPolice;
    }

    public void setTimeoutPolice(RetryBuilder.TimeoutPolice timeoutPolice) {
        this.timeoutPolice = timeoutPolice;
    }

    RetryConfig() {
    }

    public RetryConfig(Integer retry, long timeLimitMilli, ExecutorService executorService, long delayMilli, RetryBuilder.TimeoutPolice timeoutPolice) {
        this.retry = retry;
        this.timeLimitMilli = timeLimitMilli;
        this.executorService = executorService;
        this.delayMilli = delayMilli;
        this.timeoutPolice = timeoutPolice;
    }
}
