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
@SuppressWarnings("WeakerAccess")
class RetryLoop {

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
        T t = null;
        Integer num = retryConfig.getNum();
        int i = 0;
        long start = System.nanoTime();
        for (; state == State.RUNNING; ) {
            try {
                i++;
                t = retry.proceed();
            } catch (InterruptedException in) {
                break;
            } catch (Throwable e) {
                retry.whenError(e);
            }
            if (retry.canOutBreak(t, i, System.nanoTime() - start)) {
                break;
            }
            if (num > 0 && i >= num) {
                break;
            }
        }
        return retry.whenFinish(t);
    }

    enum State {
        RUNNING, STOP
    }
}
