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

    /*重试次数*/
    int retry() default 0;

    /*重试时间限制*/
    long timeLimitMilli() default 0;

    /*重试间隔*/
    long delayMilli() default 0;

    /*重试模式*/
    RetryModule module() default RetryModule.PROCEED;

    /*快速失败的错误*/
    Class<? extends Exception>[] failOn() default {};

    /*忽略的错误*/
    Class<? extends Exception>[] continueOn() default {};
}
