package com.dot.fashion.retrieval.core.api;

import com.dot.fashion.retrieval.core.exception.ProceedException;
import com.dot.fashion.retrieval.core.exception.StopException;

import java.util.function.Supplier;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@FunctionalInterface
public interface ConditionRetry<T> extends Retry<T>, Supplier<T> {

    @Override
    default T proceed(int round, long nanos) throws ProceedException, StopException {
        return get();
    }

}
