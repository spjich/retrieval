package com.dot.fashion;

import org.junit.Assert;
import org.junit.Test;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class ProceedTest {

    @Test
    public void simple() {
        new RetryBuilder().build().proceed(() -> {
            System.out.println("success");
            return "";
        });
    }


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
                            public boolean postCondition(Integer ret, int round, long nanos) {
                                System.out.println(round);
                                return false;
                            }
                        }));
    }

    @Test
    public void proceedForever() throws InterruptedException {
        new Thread(() -> {
            RetryConfig config = new RetryConfig();
            config.setNum(-1);
            long id = Thread.currentThread().getId();
            System.out.println(
                    new RetryBuilder().setConfig(config).build().proceed(
                            new Retry<Integer>() {
                                @Override
                                public Integer proceed() {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    System.out.println("success");
                                    return 1;
                                }

                                @Override
                                public Integer whenFinish(Integer ret) {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    System.out.println("finish");
                                    return 999;
                                }

                                @Override
                                public boolean postCondition(Integer ret, int round, long nanos) {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    System.out.println(round);
                                    return false;
                                }
                            }));
        }).start();
        Thread.sleep(5000L);
    }


}
