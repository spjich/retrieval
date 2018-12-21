package com.dot.fashion;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class SyncTest {

    @Test
    public void sync() throws InterruptedException, ExecutionException, TimeoutException {
        RetryConfig config = new RetryConfig();
        config.setNum(2);
        config.setExecutorService(Executors.newCachedThreadPool());
        System.out.println("主线程id:" + Thread.currentThread().getId());
        System.out.println("返回结果" + new RetryBuilder().setConfig(config).build().sync(() -> {
            System.out.println("执行线程id:" + Thread.currentThread().getId());
            return "success";
        }));
    }

    @Test(timeout = 5000)
    public void syncWithTimeout() throws InterruptedException, ExecutionException {
        RetryConfig config = new RetryConfig();
        config.setNum(2);
        config.setTimeLimitMilli(2000);
        config.setExecutorService(Executors.newCachedThreadPool());
        System.out.println("主线程id:" + Thread.currentThread().getId());
        System.out.println("返回结果" + new RetryBuilder().setConfig(config).build().sync(new Retry<String>() {
            @Override
            public String proceed() throws InterruptedException {
                System.out.println("执行线程id:" + Thread.currentThread().getId());
                Thread.sleep(300000);
                return "success";
            }

            @Override
            public void whenTimeout() {
                System.out.println("执行线程id:" + Thread.currentThread().getId() + "|timeout");
            }
        }));
    }
}
