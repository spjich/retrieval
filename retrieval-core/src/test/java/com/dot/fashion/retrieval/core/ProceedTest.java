package com.dot.fashion.retrieval.core;

import com.dot.fashion.retrieval.core.api.Retryable;
import com.dot.fashion.retrieval.core.builder.RetryBuilder;
import com.dot.fashion.retrieval.core.exception.ProceedException;
import org.junit.Assert;
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
public class ProceedTest {
    private Logger logger = LoggerFactory.getLogger(ProceedTest.class);

    @Test
    public void simple() {
        new RetryBuilder().retry(10).delay(1000).timeout(5000).pool(Executors.newSingleThreadExecutor()).build().async((round, nanos) -> "success");
        new RetryBuilder().build().proceed((round, nanos) -> "success");
        Class[] failOn = {IllegalAccessException.class};
        Class[] continueOn = {IllegalArgumentException.class};
        new RetryBuilder()
                .withCondition()
                .failOn(failOn)
                .continueOn(continueOn)
                .build()
                .proceed(() -> "success");
    }


    @Test
    public void proceed() {
        RetryConfig config = new RetryConfig();
        config.setRetry(1);
        config.setDelayMilli(5000);
        logger.info("" +
                new RetryBuilder().setConfig(config).build().proceed(
                        new Retryable<Integer>() {
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
                                return true;
                            }

                            @Override
                            public boolean preCondition(int round, long nanos) {
                                return true;
                            }
                        }));
    }

    @Test
    public void proceedForever() throws InterruptedException {
        new Thread(() -> {
            RetryConfig config = new RetryConfig();
            config.setRetry(-1);
            long id = Thread.currentThread().getId();
            logger.info("" +
                    new RetryBuilder().setConfig(config).build().proceed(
                            new Retryable<Integer>() {
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

    @Test
    public void conditionProceed() {
        long invokerId = Thread.currentThread().getId();
        Assert.assertEquals(new RetryBuilder().withCondition().build().proceed(() -> {
            Assert.assertEquals(invokerId, Thread.currentThread().getId());
            return "success";
        }), "success");
        Assert.assertEquals(new RetryBuilder().withCondition().build().proceed(() -> {
            System.out.println(1 / 0);
            return "success";
        }), null);
        Assert.assertEquals(new RetryBuilder().retry(2).withCondition().continueOn(new Class[]{ArithmeticException.class}).build().proceed(() -> {
            System.out.println("execute");
            System.out.println(1 / 0);
            return "success";
        }), null);
        try {
            new RetryBuilder().retry(2).withCondition().failOn(new Class[]{ArithmeticException.class}).build().proceed(() -> {
                System.out.println(1 / 0);
                return "success";
            });
        } catch (Exception e) {
            Assert.assertEquals(e.getCause().getClass(), ArithmeticException.class);
        }
    }

    @Test
    public void testInterrupt() throws InterruptedException {
        interruptCBProceed();
        interruptConditionProceed();
        interruptConditionSync();
        interruptConditionSyncTimeout();
        interruptCBSyncTimeout();
        interruptAsyncTimeout();
        Thread.sleep(2000);
    }

    private void interruptAsyncTimeout() throws InterruptedException {
        Thread t2 = new Thread(() -> new RetryBuilder().timeout(1000).build().async((round, nanos) -> {
            Thread.sleep(5000);
            return "success";
        }));
        t2.start();
        Thread.sleep(1000);
        t2.interrupt();
    }

    private void interruptCBSyncTimeout() {
        Thread t = new Thread(() -> {
            try {
                new RetryBuilder().timeout(1000).build().sync((round, nanos) -> {
                    Thread.sleep(5000);
                    return "success";
                });
            } catch (ExecutionException | InterruptedException e) {
                Assert.fail();
            } catch (TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        });
        t.start();
    }

    private void interruptConditionSyncTimeout() {
        Thread t = new Thread(() -> {
            try {
                new RetryBuilder().timeout(3000).withCondition().build().sync(() -> {
                    Thread.sleep(5000);
                    return "success";
                });
            } catch (InterruptedException | ExecutionException e) {
                Assert.fail();
            } catch (TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        });
        t.start();
    }

    private void interruptConditionSync() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                new RetryBuilder().withCondition().build().sync(() -> {
                    Thread.sleep(5000);
                    return "success";
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException | TimeoutException e) {
                Assert.fail();
            }
        });
        t.start();
        Thread.sleep(1000);
        t.interrupt();
    }

    private void interruptConditionProceed() {
        try {
            Thread t = new Thread(() -> new RetryBuilder().withCondition().build().proceed(() -> {
                Thread.sleep(5000);
                return "success";
            }));
            t.start();
            Thread.sleep(1000);
            t.interrupt();
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), ProceedException.class);
            Assert.assertEquals(e.getCause().getClass(), InterruptedException.class);
        }
    }

    private void interruptCBProceed() {
        try {
            Thread t = new Thread(() -> new RetryBuilder().build().proceed((round, nanos) -> {
                Thread.sleep(5000);
                return "success";
            }));
            t.start();
            Thread.sleep(1000);
            t.interrupt();
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), ProceedException.class);
            Assert.assertEquals(e.getCause().getClass(), InterruptedException.class);
        }
    }
}
