package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.Retry;
import com.dot.fashion.retrieval.core.RetryBuilder;
import com.dot.fashion.retrieval.core.RetryLoop;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
final class RetrievalParser {

    static <T> RetrievalSpringContext parse(Retrieval retrieval) throws IllegalAccessException, InstantiationException {
        RetryLoop retryLoop = new RetryBuilder()
                .retry(retrieval.retry())
                .delay(retrieval.delayMilli())
                .timeout(retrieval.timeLimitMilli()).build();
        Retry<T> retry = retrieval.logic().newInstance();
        return new RetrievalSpringContext(retrieval.module(), retryLoop, retry);
    }

}
