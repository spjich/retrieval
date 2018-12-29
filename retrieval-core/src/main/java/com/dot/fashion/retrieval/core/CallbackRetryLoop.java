package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.Retry;
import com.dot.fashion.retrieval.core.api.RetryAble;

import java.util.concurrent.ExecutionException;

/**
 * title:重复循环
 * author:吉
 * since:2018/12/19
 */

public final class CallbackRetryLoop extends RetryAble {


    public CallbackRetryLoop(RetryConfig retryConfig) {
        super(retryConfig);
    }

    public <T> T proceed(Retry<T> retry) {
        return super.proceed(retry);
    }

    public <T> T sync(Retry<T> retry) throws InterruptedException, ExecutionException {
        return super.sync(retry);
    }

    public <T> void async(Retry<T> retry) {
        super.async(retry);
    }

    /**
     * 重试循环
     *
     * @param retry
     * @param <T>
     * @return
     */
    protected <T> T loop(Retry<T> retry) {
        hook = Thread.currentThread();
        state = State.RUNNING;
        startNanos = System.nanoTime();
        T t = null;
        Integer retryNum = retryConfig.getRetry();
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
            if (sleepIfInterrupt()) break;
        }
        return retry.whenFinish(t, round, diff());
    }
}
