package com.dot.fashion;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * title:
 * author:吉
 * since:2018/12/19
 */
public class AsyncTest {

    @Test(timeout = 2200)
    public void asyncTimeLimit() throws InterruptedException {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(Executors.newCachedThreadPool());
        retryConfig.setNum(-1);
        retryConfig.setTimeLimitMilli(2000);
        new RetryBuilder().build().async(() -> {
            TimeUnit.HOURS.sleep(1);
            return "";
        });
        new RetryBuilder().build().async(() -> {
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
        retryConfig.setNum(-1);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed() throws InterruptedException {
                        System.out.println("id=" + Thread.currentThread().getId() + "|async A");
                        TimeUnit.HOURS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        System.out.println("id=" + Thread.currentThread().getId() + "|timeout A");
                    }
                }
        );
        //6s后再执行一个任务
        TimeUnit.SECONDS.sleep(6);
        System.out.println("启动第二个任务");
        retryConfig.setTimeLimitMilli(5000);
        new RetryBuilder().setConfig(retryConfig).build().async(new Retry<Integer>() {
            @Override
            public Integer proceed() throws InterruptedException {
                System.out.println("id=" + Thread.currentThread().getId() + "|async B");
                TimeUnit.HOURS.sleep(1);
                return null;
            }

            @Override
            public void whenTimeout() {
                System.out.println("id=" + Thread.currentThread().getId() + "|timeout B");
            }
        });
        System.out.println("id=" + Thread.currentThread().getId() + "|主线程完成");
        Thread.sleep(10000);
    }

    @Test
    public void asyncAtOnce() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setExecutorService(executorService);
        //5s超时
        retryConfig.setTimeLimitMilli(5000);
        retryConfig.setNum(-1);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed() throws InterruptedException {
                        System.out.println("id=" + Thread.currentThread().getId() + "|async A");
                        TimeUnit.SECONDS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        System.out.println("id=" + Thread.currentThread().getId() + "|timeout A");
                    }

                    @Override
                    public boolean canOutBreak(Integer ret, int round, long nanos) {
                        return false;
                    }
                }
        );
        retryConfig.setTimeLimitMilli(8000);
        new RetryBuilder().setConfig(retryConfig).build().async(
                new Retry<Integer>() {
                    @Override
                    public Integer proceed() throws InterruptedException {
                        System.out.println("id=" + Thread.currentThread().getId() + "|async B");
                        TimeUnit.SECONDS.sleep(1);
                        return null;
                    }

                    @Override
                    public void whenTimeout() {
                        Assert.fail();
                        System.out.println("id=" + Thread.currentThread().getId() + "|timeout B");
                    }

                    @Override
                    public boolean canOutBreak(Integer ret, int round, long nanos) {
                        return round == 7;
                    }
                }
        );

        System.out.println("主线程执行完毕");
        TimeUnit.SECONDS.sleep(10);
    }
}