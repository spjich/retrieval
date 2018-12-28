package com.dot.fashion.retrieval.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetrievalAnnotationAspect {

    public RetrievalAnnotationAspect() {
        System.out.println("初始化");
    }

    @Pointcut("@annotation(com.dot.fashion.retrieval.spring.annotation.Retrieval)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object method(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("aroundssss");
        return pjp.proceed();
    }
}