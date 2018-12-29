package com.dot.fashion.retrieval.core.api;

import com.dot.fashion.retrieval.core.CallbackRetryLoop;
import com.dot.fashion.retrieval.core.RetryConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * title:
 * author:吉
 * since:2018/12/29
 */
@SuppressWarnings({"JavaDoc"})
public abstract class RetryAble {
    protected long startNanos = System.nanoTime();
    protected RetryConfig retryConfig;
    protected Thread hook;
    protected volatile CallbackRetryLoop.State state;

    protected RetryAble(RetryConfig retryConfig) {
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
     */
    protected <T> T proceed(Retry<T> retry) {
        return adapt(retry);
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
     */
    protected <T> T sync(Retry<T> retry) throws InterruptedException, ExecutionException {
        CompletableFuture<T> completableFuture = CompletableFuture
                .supplyAsync(() -> adapt(retry), retryConfig.getExecutorService());
        long timeLimit = retryConfig.getTimeLimitMilli();
        if (timeLimit > 0) {
            try {
                return completableFuture.get(timeLimit, MILLISECONDS);
            } catch (TimeoutException e) {
                //停止任务
                stop();
                retry.whenTimeout();
                return null;
            }
        } else {
            return completableFuture.get();
        }
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
    protected <T> void async(Retry<T> retry) {
        CompletableFuture<Void> retFuture =
                CompletableFuture.runAsync(() -> adapt(retry), retryConfig.getExecutorService());
        if (retryConfig.getTimeLimitMilli() > 0) {
            CompletableFuture<Void> promise = new CompletableFuture<>();
            CompletableFuture.runAsync(() -> {
                try {
                    MILLISECONDS.sleep(retryConfig.getTimeLimitMilli());
                } catch (InterruptedException ignored) {
                }
                promise.completeExceptionally(new TimeoutException("Timeout"));
            });
            retFuture.applyToEither(promise, Function.identity()).exceptionally(throwable -> {
                stop();
                retry.whenTimeout();
                return null;
            });
        }
    }


    private <T> T adapt(Retry<T> retry) {
        if (retry instanceof ConditionRetry) {
            return loop((ConditionRetry<T>) retry);
        } else {
            return loop(retry);
        }
    }

    private void stop() {
        hook.interrupt();
        state = CallbackRetryLoop.State.STOP;
    }

    protected long diff() {
        return System.nanoTime() - startNanos;
    }

    protected enum State {
        RUNNING, STOP
    }

    protected boolean sleepIfInterrupt() {
        try {
            TimeUnit.MILLISECONDS.sleep(retryConfig.getDelayMilli());
        } catch (InterruptedException e) {
            return true;
        }
        return false;
    }


    protected abstract <T> T loop(Retry<T> retry);

}
