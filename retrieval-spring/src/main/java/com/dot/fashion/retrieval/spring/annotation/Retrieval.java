package com.dot.fashion.retrieval.spring.annotation;

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

    int FOREVER = -1;

    int retry() default 0;

    long timeLimitMilli() default 0;

    long delayMilli() default 0;

    RetryModule module() default RetryModule.PROCEED;

    Class<? extends Exception>[] failOn() default {};

    Class<? extends Exception>[] continueOn() default {};
}
