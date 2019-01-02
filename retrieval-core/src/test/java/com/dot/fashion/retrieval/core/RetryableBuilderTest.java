package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.builder.RetryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class RetryableBuilderTest {

    @Test
    public void buildTest() {
        Assert.assertEquals(new RetryBuilder().getRetryConfig().getExecutorService(), new RetryBuilder().getRetryConfig().getExecutorService());
    }
}