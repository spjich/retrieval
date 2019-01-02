package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.ConditionRetryable;
import com.dot.fashion.retrieval.core.api.Retryable;
import com.dot.fashion.retrieval.core.builder.RetryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    @Test
    public void conditionSync() throws ExecutionException, InterruptedException {
        long invokerId = Thread.currentThread().getId();
        Assert.assertEquals(new RetryBuilder().withCondition().build().sync(() -> {
            Assert.assertNotEquals(invokerId, Thread.currentThread().getId());
            return "success";
        }), "success");
        Assert.assertEquals(new RetryBuilder().withCondition().build().sync(() -> {
            System.out.println(1 / 0);
            return "success";
        }), null);
        Assert.assertEquals(new RetryBuilder().retry(2).withCondition().continueOn(new Class[]{ArithmeticException.class}).build().sync(() -> {
            System.out.println("execute");
            System.out.println(1 / 0);
            return "success";
        }), null);
        try {
            new RetryBuilder().retry(2).withCondition().failOn(new Class[]{ArithmeticException.class}).build().sync(() -> {
                System.out.println(1 / 0);
                return "success";
            });
        } catch (Exception e) {
            Assert.assertEquals(e.getCause().getCause().getClass(), ArithmeticException.class);
        }

        new RetryBuilder().retry(2).timeout(3000).withCondition().continueOn(new Class[]{ArithmeticException.class}).build().sync(new ConditionRetryable<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    logger.info("interrupted");
                    return "break";
                }
                System.out.println(1 / 0);
                return "success";
            }

            @Override
            public void whenTimeout() {
                Assert.assertEquals(invokerId, Thread.currentThread().getId());
                logger.info("timeout");
            }

        });
        TimeUnit.SECONDS.sleep(1);

        try {
            new RetryBuilder().withCondition().continueOn(new Class[]{ArithmeticException.class}).build().sync(
                    (ConditionRetryable<String>) () -> {
                        throw new RuntimeException("not in condition");
                    }

            );
        } catch (Exception e) {
            Assert.assertEquals(e.getCause().getCause().getMessage(), "not in condition");
        }
    }
}
