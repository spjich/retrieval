package com.dot.fashion.retrieval.spring;

import java.lang.annotation.*;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Retrieval {
}
