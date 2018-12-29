package com.dot.fashion.retrieval.spring;

import com.dot.fashion.retrieval.spring.annotation.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * title:
 * author:吉
 * since:2018/12/29
 */
@SpringBootApplication
public class MainTest implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * title:
     * author:吉
     * since:2018/12/28
     */
    private Logger logger = LoggerFactory.getLogger(com.dot.fashion.retrieval.spring.ApplicationTest.class);
    @Autowired
    private TestService testService;

    public void test() {
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("执行结果：" + testService.test(1));

    }

    public static void main(String[] args) {
        SpringApplication.run(com.dot.fashion.retrieval.spring.ApplicationTest.class);
    }

}
