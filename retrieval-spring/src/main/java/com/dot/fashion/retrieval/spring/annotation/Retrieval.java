package com.dot.fashion.retrieval.spring.annotation;

import com.dot.fashion.retrieval.core.Retry;

import java.lang.annotation.*;

/**
 * title: 注解配置
 * author:吉
 * since:2018/12/28
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Retrieval {

    int retry() default 0;

    long timeLimitMilli() default 0;

    long delayMilli() default 0;

    RetryModule module() default RetryModule.PROCEED;

    String pool() default "";

    Class<? extends Retry> logic();
}
