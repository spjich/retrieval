package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.ConditionRetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public final class RetrievalSpringContext {

    private RetryModule module;

    private ConditionRetryLoop retryLoop;


    public RetryModule getModule() {
        return module;
    }

    public void setModule(RetryModule module) {
        this.module = module;
    }

    public ConditionRetryLoop getRetryLoop() {
        return retryLoop;
    }

    public void setRetryLoop(ConditionRetryLoop retryLoop) {
        this.retryLoop = retryLoop;
    }

    RetrievalSpringContext(RetryModule module, ConditionRetryLoop retryLoop) {
        this.module = module;
        this.retryLoop = retryLoop;
    }
}
