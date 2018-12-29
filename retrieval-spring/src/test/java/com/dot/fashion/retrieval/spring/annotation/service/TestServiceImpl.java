package com.dot.fashion.retrieval.spring.annotation.service;

import com.dot.fashion.retrieval.spring.annotation.Retrieval;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * title:
 * author:吉
 * since:2018/12/28
 */
@Service
public class TestServiceImpl implements TestService {

    @Retrieval(retry = 10, delayMilli = 1000)
    public List<String> test(int a) {
        System.out.println("执行test");
        if (a == 1) {
            throw new RuntimeException();
        }
        if (a == 2) {
            throw new IllegalArgumentException();
        }
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        return strings;
    }

//    public static void main(String[] args) throws NoSuchMethodException {
//     Method[] methods= TestServiceImpl.class.getDeclaredMethods();
//        System.out.println(methods);
//     }
}
