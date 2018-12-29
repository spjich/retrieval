package com.dot.fashion.retrieval.core;

import java.util.concurrent.ExecutorService;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
class RetryConfig {

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
    private Class<? extends Exception>[] continueWhen;


    Integer getRetry() {
        return retry;
    }

    void setRetry(Integer retry) {
        this.retry = retry;
    }

    long getTimeLimitMilli() {
        return timeLimitMilli;
    }

    void setTimeLimitMilli(long timeLimitMilli) {
        this.timeLimitMilli = timeLimitMilli;
    }


    ExecutorService getExecutorService() {
        return executorService;
    }

    void setExecutorService(ExecutorService executorService) {
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

    public Class<? extends Exception>[] getContinueWhen() {
        return continueWhen;
    }

    public void setContinueWhen(Class<? extends Exception>[] continueWhen) {
        this.continueWhen = continueWhen;
    }

    RetryConfig() {
    }

    public RetryConfig(Integer num, long timeLimitMilli, ExecutorService executorService, long delayMilli) {
        this.retry = num;
        this.timeLimitMilli = timeLimitMilli;
        this.executorService = executorService;
        this.delayMilli = delayMilli;
    }
}
