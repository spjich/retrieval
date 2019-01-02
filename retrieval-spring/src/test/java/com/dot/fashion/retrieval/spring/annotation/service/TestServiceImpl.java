package com.dot.fashion.retrieval.spring.annotation.service;

import com.dot.fashion.retrieval.spring.annotation.Retrieval;
import com.dot.fashion.retrieval.spring.annotation.RetryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@Service
public class TestServiceImpl implements TestService {
    private Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    @Retrieval(retry = 3,
            delayMilli = 1000,
            module = RetryModule.ASYNC,
            timeLimitMilli = 15000,
            failOn = IllegalArgumentException.class,
            continueOn = IllegalAccessException.class)
    public List<String> test(int a) {
        logger.info("执行test");
        if (a == 1) {
            throw new RuntimeException();
        } else if (a == 2) {
            throw new IllegalArgumentException();
        } else if (a == 3) {
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        } else if (a == 4) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        logger.info("service return");
        return strings;
    }
}
