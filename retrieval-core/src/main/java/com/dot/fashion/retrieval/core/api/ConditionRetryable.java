package com.dot.fashion.retrieval.core.api;

import com.dot.fashion.retrieval.core.exception.ProceedException;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@FunctionalInterface
public interface ConditionRetryable<T> extends Retryable<T> {

    @Override
    default T proceed(int round, long nanos) throws ProceedException {
        try {
            return get();
        } catch (Throwable throwable) {
            throw new ProceedException(throwable);
        }
    }

    T get() throws Throwable;
}
