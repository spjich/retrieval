package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.builder.RetryBuilder;
import com.dot.fashion.retrieval.core.CallbackRetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public final class RetrievalParser {

    public static RetrievalSpringContext parse(Retrieval retrieval) {
        CallbackRetryLoop retryLoop = new RetryBuilder()
                .retry(retrieval.retry())
                .delay(retrieval.delayMilli())
                .failOn(retrieval.failOn())
                .continueOn(retrieval.continueOn())
                .timeout(retrieval.timeLimitMilli()).build();
        return new RetrievalSpringContext(retrieval.module(), retryLoop);
    }

}
