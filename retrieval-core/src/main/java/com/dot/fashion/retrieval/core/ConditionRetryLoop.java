package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.Retry;
import com.dot.fashion.retrieval.core.api.RetryAble;
import com.dot.fashion.retrieval.core.api.ConditionRetry;
import com.dot.fashion.retrieval.core.exception.ProceedException;
import com.dot.fashion.retrieval.core.exception.StopException;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * title:
 * author:吉
 * since:2018/12/29
 */
public final class ConditionRetryLoop extends RetryAble {
    public ConditionRetryLoop(RetryConfig retryConfig) {
        super(retryConfig);
    }

    public <T> T proceed(ConditionRetry<T> retry) {
        return super.proceed(retry);
    }

    public <T> T sync(ConditionRetry<T> retry) throws InterruptedException, ExecutionException {
        return super.sync(retry);
    }

    public <T> void async(ConditionRetry<T> retry) {
        super.async(retry);
    }

    @Override
    protected <T> T loop(Retry<T> retry) {
        hook = Thread.currentThread();
        state = RetryAble.State.RUNNING;
        startNanos = System.nanoTime();
        Integer retryNum = retryConfig.getRetry();
        int round = 0;
        Class<? extends Exception>[] failOn = retryConfig.getFailOn();
        Class[] continueOn = retryConfig.getContinueOn();
        for (; state == RetryAble.State.RUNNING; ) {
            try {
                return ((ConditionRetry<T>) retry).proceed(round, diff());
            } catch (StopException in) {
                break;
            } catch (ProceedException e) {
                if (Stream.of(failOn).anyMatch((clz) -> e.getCause().getClass() == clz)) {
                    throw new RuntimeException(e);
                }
                if (continueOn.length != 0) {
                    if (Stream.of(continueOn).noneMatch((clz) -> e.getCause().getClass() == clz)) {
                        throw new RuntimeException(e);
                    }
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
