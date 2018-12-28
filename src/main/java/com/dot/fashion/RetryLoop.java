package com.dot.fashion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * title:重复循环
 * author:吉
 * since:2018/12/19
 */
@SuppressWarnings({"WeakerAccess", "JavaDoc"})
public final class RetryLoop {

    public static long startNanos = System.nanoTime();
    private RetryConfig retryConfig;
    private Thread hook;
    private volatile State state;

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
     */
    public <T> T sync(Retry<T> retry) throws InterruptedException, ExecutionException {
        CompletableFuture<T> completableFuture = CompletableFuture
                .supplyAsync(() -> loop(retry), retryConfig.getExecutorService());
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
    public <T> void async(Retry<T> retry) {
        CompletableFuture<Void> retFuture =
                CompletableFuture.runAsync(() -> loop(retry), retryConfig.getExecutorService());
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

    private void stop() {
        hook.interrupt();
        state = State.STOP;
    }

    /**
     * 重试循环
     *
     * @param retry
     * @param <T>
     * @return
     */
    private <T> T loop(Retry<T> retry) {
        hook = Thread.currentThread();
        state = State.RUNNING;
        startNanos = System.nanoTime();
        T t = null;
        Integer retryNum = retryConfig.getNum();
        int round = 0;
        for (; state == State.RUNNING; ) {
            try {
                if (!retry.preCondition(round, diff())) {
                    break;
                }
                t = retry.proceed(round, diff());
            } catch (InterruptedException in) {
                break;
            } catch (Exception e) {
                if (retry.whenError(e, round, diff())) {
                    break;
                }
            }
            if (retry.postCondition(t, round, diff())) {
                break;
            }
            round++;
            if (retryNum >= 0 && round > retryNum) {
                break;
            }
            try {
                MILLISECONDS.sleep(retryConfig.getDelayMilli());
            } catch (InterruptedException e) {
                break;
            }
        }
        return retry.whenFinish(t, round, diff());
    }

    private long diff() {
        return System.nanoTime() - startNanos;
    }

    enum State {
        RUNNING, STOP
    }
}
