package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.RetryLoop;
import com.dot.fashion.retrieval.core.api.Retryable;
import com.dot.fashion.retrieval.core.exception.ProceedException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * title:重复循环
 * author:吉
 * since:2018/12/19
 */

public final class CallbackRetryLoop extends RetryLoop {


    public CallbackRetryLoop(RetryConfig retryConfig) {
        super(retryConfig);
    }

    public <T> T proceed(Retryable<T> retryable) {
        return super.proceed(retryable);
    }

    public <T> T sync(Retryable<T> retryable) throws InterruptedException, ExecutionException, TimeoutException {
        return super.sync(retryable);
    }

    public <T> void async(Retryable<T> retryable) {
        super.async(retryable);
    }

    /**
     * 重试循环
     *
     * @param retryable
     * @param <T>
     * @return
     */
    protected <T> T loop(Retryable<T> retryable) {
        hook = Thread.currentThread();
        state = State.RUNNING;
        startNanos = System.nanoTime();
        T t = null;
        Integer retryNum = retryConfig.getRetry();
        int round = 0;
        for (; state == State.RUNNING; ) {
            try {
                if (!retryable.preCondition(round, diff())) {
                    break;
                }
                t = retryable.proceed(round, diff());
                if (retryable.postCondition(t, round, diff())) {
                    break;
                }
            } catch (ProceedException pro) {
                throw pro;
            } catch (InterruptedException in) {
                throw new ProceedException(in);
            } catch (Exception e) {
                try {
                    if (retryable.whenError(e, round, diff())) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            round++;
            if (retryNum >= 0 && round > retryNum) {
                break;
            }
            if (sleepIfInterrupt()) break;
        }
        return retryable.whenFinish(t, round, diff());
    }
}
