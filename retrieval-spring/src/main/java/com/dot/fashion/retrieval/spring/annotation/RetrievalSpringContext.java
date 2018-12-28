package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.Retry;
import com.dot.fashion.retrieval.core.RetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public class RetrievalSpringContext<T> {

    private RetryModule module;

    private RetryLoop retryLoop;

    private Retry<T> retry;

    public RetryModule getModule() {
        return module;
    }

    public void setModule(RetryModule module) {
        this.module = module;
    }

    public RetryLoop getRetryLoop() {
        return retryLoop;
    }

    public void setRetryLoop(RetryLoop retryLoop) {
        this.retryLoop = retryLoop;
    }

    public Retry<T> getRetry() {
        return retry;
    }


    public RetrievalSpringContext(RetryModule module, RetryLoop retryLoop, Retry<T> retry) {
        this.module = module;
        this.retryLoop = retryLoop;
        this.retry = retry;
    }

    public void setRetry(Retry<T> retry) {
        this.retry = retry;
    }
}
