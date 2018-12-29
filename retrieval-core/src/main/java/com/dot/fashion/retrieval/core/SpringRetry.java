package com.dot.fashion.retrieval.core;

import java.util.function.Supplier;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public class SpringRetry<T> implements Retry<T> {

    private Supplier<T> supplier;

    public SpringRetry(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T proceed(int round, long nanos) throws Exception {
        return supplier.get();
    }
}
