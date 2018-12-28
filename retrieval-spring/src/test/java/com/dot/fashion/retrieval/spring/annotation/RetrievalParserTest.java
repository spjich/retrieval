package com.dot.fashion.retrieval.spring.annotation;


import com.dot.fashion.retrieval.spring.annotation.service.TestService;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
public class RetrievalParserTest {


    @Test
    public void parse() throws InstantiationException, IllegalAccessException {
        Method[] declaredMethods = TestService.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Retrieval retrieval = method.getAnnotation(Retrieval.class);
            RetrievalSpringContext con = RetrievalParser.parse(retrieval);
            System.out.println(con.getModule());
            System.out.println(con.getRetryLoop().proceed(con.getRetry()).getClass().getSimpleName());
        }
//        Retry<String> retry = AnnotationTest.class.newInstance();
//        System.out.println(new RetryBuilder().build().proceed(retry));
    }
}