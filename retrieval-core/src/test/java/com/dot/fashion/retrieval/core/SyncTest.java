package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.Retryable;
import com.dot.fashion.retrieval.core.builder.RetryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class SyncTest {

    private Logger logger = LoggerFactory.getLogger(SyncTest.class);

    @Test
    public void sync() throws InterruptedException, ExecutionException, TimeoutException {
        RetryConfig config = new RetryConfig();
        config.setRetry(2);//重试2次
        config.setDelayMilli(5000);
        config.setExecutorService(Executors.newCachedThreadPool());
        logger.info("主线程id:" + Thread.currentThread().getId());
        logger.info("返回结果" + new RetryBuilder().setConfig(config).build().sync(
                new Retryable<String>() {
                    @Override
                    public String proceed(int round, long nanos) throws Exception {
                        logger.info("执行线程id:" + Thread.currentThread().getId());
                        return "success";
                    }

                    @Override
                    public boolean postCondition(String ret, int round, long nanos) {
                        return false;
                    }
                }

        ));
    }

    @Test(timeout = 5000)
    public void syncWithTimeout() throws InterruptedException, ExecutionException {
        RetryConfig config = new RetryConfig();
        config.setRetry(2);
        config.setDelayMilli(5000);
        config.setTimeLimitMilli(3000);
        config.setExecutorService(Executors.newCachedThreadPool());
        config.setTimeoutPolice(RetryBuilder.TimeoutPolice.InterruptAndSetFlag);
        logger.info("主线程id:" + Thread.currentThread().getId());
        logger.info("返回结果" + new RetryBuilder().setConfig(config).build().sync(new Retryable<String>() {
            @Override
            public String proceed(int round, long nanos) throws InterruptedException {
                logger.info("执行线程id:{},round{}", Thread.currentThread().getId(), round);
                Thread.sleep(300000);
                return "success";
            }

            @Override
            public void whenTimeout() {
                logger.info("执行线程id:" + Thread.currentThread().getId() + "|timeout");
            }
        }));
    }
}
