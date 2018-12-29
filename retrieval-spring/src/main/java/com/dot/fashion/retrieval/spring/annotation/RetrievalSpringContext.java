package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.RetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public final class RetrievalSpringContext {

    private RetryModule module;

    private RetryLoop retryLoop;


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

    RetrievalSpringContext(RetryModule module, RetryLoop retryLoop) {
        this.module = module;
        this.retryLoop = retryLoop;
    }
}
