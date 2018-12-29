package com.dot.fashion.retrieval.spring.aop;

import com.dot.fashion.retrieval.core.SpringRetry;
import com.dot.fashion.retrieval.spring.annotation.Retrieval;
import com.dot.fashion.retrieval.spring.annotation.RetrievalParser;
import com.dot.fashion.retrieval.spring.annotation.RetrievalSpringContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@SuppressWarnings("unchecked")
public class RetrievalAnnotationAspect {

    @Pointcut("@annotation(com.dot.fashion.retrieval.spring.annotation.Retrieval)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public <T> T method(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Retrieval retrieval = method.getAnnotation(Retrieval.class);
        RetrievalSpringContext context = RetrievalParser.parse(retrieval);
        SpringRetry<T> retry = new SpringRetry<>(() -> {
            try {
                return (T) pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
        switch (context.getModule()) {
            case SYNC:
                return context.getRetryLoop().sync(retry);
            case PROCEED:
                return context.getRetryLoop().proceed(retry);
            case ASYNC:
                context.getRetryLoop().async(retry);
                return null;
            default:
                return (T) pjp.proceed();
        }

    }
}