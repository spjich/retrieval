package com.dot.fashion.retrieval.core;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class AsyncTest {
    private Logger logger = LoggerFactory.getLogger(AsyncTest.class);

    @Test(timeout = 2200)
    public void asyncTimeLimit() throws InterruptedException {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(Executors.newCachedThreadPool());
        retryConfig.setRetry(-1);
        retryConfig.setTimeLimitMilli(2000);
        new RetryBuilder().build().async((int round, long nanos) -> {
            TimeUnit.HOURS.sleep(1);
            return "";
        });
        new RetryBuilder().build().async((int round, long nanos) -> {
            TimeUnit.HOURS.sleep(1);
            return "";
        });
        Thread.sleep(2100);
    }

    @Test(timeout = 20000)
    public void async() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(executorService);
        //5s超时
        retryConfig.setTimeLimitMilli(5000);
        retryConfig.setRetry(-1);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed(int round, long nanos) throws InterruptedException {
                        logger.info("id=" + Thread.currentThread().getId() + "|async A");
                        TimeUnit.HOURS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        logger.info("id=" + Thread.currentThread().getId() + "|timeout A");
                    }
                }
        );
        //6s后再执行一个任务
        TimeUnit.SECONDS.sleep(6);
        logger.info("启动第二个任务");
        retryConfig.setTimeLimitMilli(5000);
        new RetryBuilder().setConfig(retryConfig).build().async(new Retry<Integer>() {
            @Override
            public Integer proceed(int round, long nanos) throws InterruptedException {
                logger.info("id=" + Thread.currentThread().getId() + "|async B");
                TimeUnit.HOURS.sleep(1);
                return null;
            }

            @Override
            public void whenTimeout() {
                logger.info("id=" + Thread.currentThread().getId() + "|timeout B");
            }
        });
        logger.info("id=" + Thread.currentThread().getId() + "|主线程完成");
        Thread.sleep(10000);
    }

    @Test
    public void asyncAtOnce() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(executorService);
        //5s超时
        retryConfig.setTimeLimitMilli(5000);
        retryConfig.setRetry(-1);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed(int round, long nanos) throws InterruptedException {
                        logger.info("id=" + Thread.currentThread().getId() + "|async A");
                        TimeUnit.SECONDS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        logger.info("id=" + Thread.currentThread().getId() + "|timeout A");
                    }

                    @Override
                    public boolean postCondition(Integer ret, int round, long nanos) {
                        return false;
                    }
                }
        );
        retryConfig.setTimeLimitMilli(8000);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed(int round, long nanos) throws InterruptedException {
                        logger.info("id=" + Thread.currentThread().getId() + "|async B");
                        TimeUnit.SECONDS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        Assert.fail();
                        logger.info("id=" + Thread.currentThread().getId() + "|timeout B");
                    }

                    @Override
                    public boolean postCondition(Integer ret, int round, long nanos) {
                        return round == 7;
                    }
                }
        );

        logger.info("主线程执行完毕");
        TimeUnit.SECONDS.sleep(10);
    }
}