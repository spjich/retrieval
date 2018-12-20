package com.dot.fashion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * title:重复循环
 * author:吉
 * since:2018/12/19
 */
class RetryLoop {

    private RetryConfig retryConfig;

    RetryLoop(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    /**
     * 线程：单线程
     * 方式：同步
     * 不支持timeout
     *
     * @param retry
     * @param <T>
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public <T> T proceed(Retry<T> retry) {
        return loop(retry);
    }

    /**
     * 线程：不同线程
     * 方式：同步
     * 支持timeout
     *
     * @param retry
     * @param <T>
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public <T> T sync(Retry<T> retry) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<T> completableFuture = CompletableFuture
                .supplyAsync(() -> loop(retry), retryConfig.getExecutorService());
        long timeLimit = retryConfig.getTimeLimitMilli();
        return timeLimit > 0 ? completableFuture.get(timeLimit, MILLISECONDS) : completableFuture.get();
    }


    /**
     * 线程：不同线程
     * 方式：同步
     *
     * @param retry
     * @param <T>
     * @return
     */
    public <T> T syncSilence(Retry<T> retry) {
        try {
            return sync(retry);
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * 线程：不同线程
     * 方式：异步
     * 不支持timeout
     *
     * @param retry
     * @param <T>
     * @return
     */
    public <T> void async(Retry<T> retry) {
        CompletableFuture.supplyAsync(() -> loop(retry), retryConfig.getExecutorService());
    }

    /**
     * 重试循环
     *
     * @param retry
     * @param <T>
     * @return
     */
    private <T> T loop(Retry<T> retry) {
        T t = null;
        Integer num = retryConfig.getNum();
        int i = 0;
        long start = System.nanoTime();
        for (; ; ) {
            try {
                t = retry.proceed();
            } catch (Throwable e) {
                retry.whenError(e);
            }
            if (retry.canOutBreak(t, i, System.nanoTime() - start)) {
                break;
            }
            if (num > 0 && ++i >= num) {
                break;
            }
        }
        return retry.whenFinish(t);
    }
}
