package com.dot.fashion;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class RetryBuilderTest {

    @Test
    public void proceed() {
        RetryConfig config = new RetryConfig();
        config.setNum(2);
        System.out.println(
                new RetryBuilder().setConfig(config).build().proceed(
                        new Retry<Integer>() {
                            @Override
                            public Integer proceed() {
                                System.out.println("success");
                                return 1;
                            }

                            @Override
                            public Integer whenFinish(Integer ret) {
                                System.out.println("finish");
                                return 999;
                            }

                            @Override
                            public boolean canOutBreak(Integer ret, int round, long nanos) {
                                return false;
                            }
                        }));
    }

    @Test
    public void sync() throws InterruptedException, ExecutionException, TimeoutException {
        RetryConfig config = new RetryConfig();
        config.setNum(2);
        config.setExecutorService(Executors.newCachedThreadPool());
        System.out.println("主线程id:" + Thread.currentThread().getId());
        System.out.println(
                new RetryBuilder().setConfig(config).build().sync(
                        new Retry<Integer>() {
                            @Override
                            public Integer proceed() {
                                System.out.println("执行线程id:" + Thread.currentThread().getId());
                                throw new RuntimeException("error");
                            }

                            @Override
                            public Integer whenFinish(Integer ret) {
                                return 999;
                            }

                            @Override
                            public boolean canOutBreak(Integer ret, int round, long nanos) {
                                System.out.println(ret + "|" + round + "|" + nanos);
                                return false;
                            }

                            @Override
                            public void whenError(Throwable e) {
                                e.printStackTrace();
                            }
                        }));
    }

    @Test
    public void syncTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(Executors.newCachedThreadPool());
        retryConfig.setNum(2);
        retryConfig.setTimeLimitMilli(3000);
        new RetryBuilder().setConfig(retryConfig).build().sync(new Retry<Integer>() {
            @Override
            public Integer proceed() {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                return null;
            }

            @Override
            public boolean canOutBreak(Integer ret, int round, long nanos) {
                System.out.println("执行完成一轮");
                return false;
            }
        });
        System.out.println("主线程完成");
    }

    @Test
    public void async() throws InterruptedException {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(Executors.newCachedThreadPool());
        retryConfig.setNum(2);
        new RetryBuilder().setConfig(retryConfig).build().async(new Retry<Integer>() {
            @Override
            public Integer proceed() {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                return null;
            }

            @Override
            public boolean canOutBreak(Integer ret, int round, long nanos) {
                System.out.println(Thread.currentThread().getId() + "执行完成一轮");
                return false;
            }
        });
        System.out.println("主线程完成");
        Thread.sleep(10000);
    }
}