package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.ConditionRetryable;
import com.dot.fashion.retrieval.core.api.RetryLoop;
import com.dot.fashion.retrieval.core.api.Retryable;
import com.dot.fashion.retrieval.core.exception.ProceedException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * title:
 * author:吉
 * since:2018/12/29
 */
public final class ConditionRetryLoop extends RetryLoop {
    public ConditionRetryLoop(RetryConfig retryConfig) {
        super(retryConfig);
    }

    public <T> T proceed(ConditionRetryable<T> retry) {
        return super.proceed(retry);
    }

    public <T> T sync(ConditionRetryable<T> retry) throws InterruptedException, ExecutionException, TimeoutException {
        return super.sync(retry);
    }

    public <T> void async(ConditionRetryable<T> retry) {
        super.async(retry);
    }

    @Override
    protected <T> T loop(Retryable<T> retryable) {
        hook = Thread.currentThread();
        state = RetryLoop.State.RUNNING;
        startNanos = System.nanoTime();
        Integer retryNum = retryConfig.getRetry();
        int round = 0;
        Class<? extends Exception>[] failOn = retryConfig.getFailOn();
        Class<? extends Exception>[] continueOn = retryConfig.getContinueOn();
        for (; state == RetryLoop.State.RUNNING; ) {
            try {
                return ((ConditionRetryable<T>) retryable).proceed(round, diff());
            } catch (ProceedException e) {
                if (e.getCause().getClass() == InterruptedException.class) {
                    throw e;
                }
                if (failOn != null && Stream.of(failOn).anyMatch((clz) -> e.getCause().getClass() == clz)) {
                    throw e;
                }
                if (continueOn != null
                        && continueOn.length > 0
                        && Stream.of(continueOn)
                        .noneMatch((clz) -> e.getCause().getClass() == clz)) {
                    throw e;
                }
                round++;
                if (retryNum >= 0 && round > retryNum) {
                    break;
                }
                if (sleepIfInterrupt()) break;
            }
        }
        return null;
    }
}
