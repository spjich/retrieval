package com.dot.fashion.retrieval.spring.annotation.service;

import com.dot.fashion.retrieval.core.Retry;

import java.util.ArrayList;
import java.util.List;

public class AnnotationTest implements Retry<List> {

    @Override
    public List proceed(int round, long nanos) throws Exception {
        return new ArrayList();
    }
}
