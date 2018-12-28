package com.dot.fashion;

import java.util.concurrent.ExecutorService;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
class RetryConfig {

    //重试次数
    private Integer num;

    //整个重试等待时间
    private long timeLimitMilli;

    //异步执行线程池
    private ExecutorService executorService;

    //每次重试等待时间
    private long delayMilli;


    Integer getNum() {
        return num;
    }

    void setNum(Integer num) {
        this.num = num;
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

    RetryConfig() {
    }

    public RetryConfig(Integer num, long timeLimitMilli, ExecutorService executorService, long delayMilli) {
        this.num = num;
        this.timeLimitMilli = timeLimitMilli;
        this.executorService = executorService;
        this.delayMilli = delayMilli;
    }
}
