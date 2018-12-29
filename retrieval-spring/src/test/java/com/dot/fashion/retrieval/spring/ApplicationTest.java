package com.dot.fashion.retrieval.spring;

import com.dot.fashion.retrieval.spring.annotation.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@ComponentScan
@EnableAutoConfiguration
public class ApplicationTest {

    @Autowired
    private TestService testService;

    @Test
    public void test() {
        System.out.println(testService.test(1));
    }

}
