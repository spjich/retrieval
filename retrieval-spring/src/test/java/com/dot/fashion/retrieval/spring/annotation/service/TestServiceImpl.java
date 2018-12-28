package com.dot.fashion.retrieval.spring.annotation.service;

import com.dot.fashion.retrieval.spring.annotation.Retrieval;
import org.springframework.stereotype.Service;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@Service
public class TestServiceImpl implements TestService {
    @Retrieval(retry = 10, logic = AnnotationTest.class)
    public String test() {
        return "success";
    }
}
