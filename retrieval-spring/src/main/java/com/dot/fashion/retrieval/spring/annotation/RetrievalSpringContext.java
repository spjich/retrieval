package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.CallbackRetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public final class RetrievalSpringContext {

    private RetryModule module;

    private CallbackRetryLoop retryLoop;


    public RetryModule getModule() {
        return module;
    }

    public void setModule(RetryModule module) {
        this.module = module;
    }

    public CallbackRetryLoop getRetryLoop() {
        return retryLoop;
    }

    public void setRetryLoop(CallbackRetryLoop retryLoop) {
        this.retryLoop = retryLoop;
    }

    RetrievalSpringContext(RetryModule module, CallbackRetryLoop retryLoop) {
        this.module = module;
        this.retryLoop = retryLoop;
    }
}
