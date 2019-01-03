package com.dot.fashion.retrieval.core.api;

import com.dot.fashion.retrieval.core.CallbackRetryLoop;
import com.dot.fashion.retrieval.core.RetryConfig;
import com.dot.fashion.retrieval.core.exception.ProceedException;

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
public abstract class RetryLoop {
    protected long startNanos = System.nanoTime();
    protected RetryConfig retryConfig;
    protected Thread hook;
    protected volatile CallbackRetryLoop.State state;

    protected RetryLoop(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    /**
     * 线程：单线程
     * 方式：同步
     * 不支持timeout
     *
     * @param retryable
     * @param <T>
     * @return
     */
    protected <T> T proceed(Retryable<T> retryable) {
        return loop(retryable);
    }

    /**
     * 线程：不同线程
     * 方式：同步
     * 支持timeout
     *
     * @param retryable
     * @param <T>
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected <T> T sync(Retryable<T> retryable) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<T> completableFuture = CompletableFuture
                .supplyAsync(() -> loop(retryable), retryConfig.getExecutorService());
        long timeLimit = retryConfig.getTimeLimitMilli();
        if (timeLimit > 0) {
            try {
                return completableFuture.get(timeLimit, MILLISECONDS);
            } catch (TimeoutException e) {
                //停止任务
                stop();
                retryable.whenTimeout();
                throw e;
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
     * @param retryable
     * @param <T>
     * @return
     */
    protected <T> void async(Retryable<T> retryable) {
        CompletableFuture<Void> retFuture =
                CompletableFuture.runAsync(() -> loop(retryable), retryConfig.getExecutorService());
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
                if (throwable.getCause() != null && throwable.getCause() instanceof TimeoutException) {
                    CompletableFuture.runAsync(() -> {
                        stop();
                        retryable.whenTimeout();
                    }, retryConfig.getExecutorService());
                }
                return null;
            });
        }
    }

    private void stop() {
        switch (retryConfig.getTimeoutPolice()) {
            case SetFlag:
                state = CallbackRetryLoop.State.STOP;
                break;
            case InterruptAndSetFlag:
                hook.interrupt();
                state = CallbackRetryLoop.State.STOP;
                break;
        }
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
            throw new ProceedException(e);
        }
        return false;
    }


    protected abstract <T> T loop(Retryable<T> retryable);

}
