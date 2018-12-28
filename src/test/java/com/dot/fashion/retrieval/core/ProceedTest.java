package com.dot.fashion.retrieval.core;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class ProceedTest {
    private Logger logger = LoggerFactory.getLogger(ProceedTest.class);

    @Test
    public void simple() {
        new RetryBuilder().build().proceed((round, nanos) -> {
            logger.info("success");
            return "";
        });

        new RetryBuilder().retry(10).delay(1000).timeout(5000).pool(Executors.newSingleThreadExecutor()).build().async((round, nanos) -> "success");

    }


    @Test
    public void proceed() {
        RetryConfig config = new RetryConfig();
        config.setNum(1);
        config.setDelayMilli(5000);
        logger.info("" +
                new RetryBuilder().setConfig(config).build().proceed(
                        new Retry<Integer>() {
                            @Override
                            public Integer proceed(int round, long nanos) {
                                logger.info("success");
                                return 1;
                            }

                            @Override
                            public Integer whenFinish(Integer ret, int round, long nanos) {
                                logger.info("finish");
                                return 999;
                            }

                            @Override
                            public boolean postCondition(Integer ret, int round, long nanos) {
                                logger.info(round + "");
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
            logger.info("" +
                    new RetryBuilder().setConfig(config).build().proceed(
                            new Retry<Integer>() {
                                @Override
                                public Integer proceed(int round, long nanos) {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    logger.info("success");
                                    return 1;
                                }

                                @Override
                                public Integer whenFinish(Integer ret, int round, long nanos) {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    logger.info("finish");
                                    return 999;
                                }

                                @Override
                                public boolean postCondition(Integer ret, int round, long nanos) {
                                    Assert.assertEquals(id, Thread.currentThread().getId());
                                    logger.info(round + "");
                                    return false;
                                }
                            }));
        }).start();
        Thread.sleep(5000L);
    }


}
