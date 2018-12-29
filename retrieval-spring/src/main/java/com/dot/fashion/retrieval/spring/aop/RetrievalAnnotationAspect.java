package com.dot.fashion.retrieval.spring.aop;

import com.dot.fashion.retrieval.core.api.ConditionRetry;
import com.dot.fashion.retrieval.core.exception.ProceedException;
import com.dot.fashion.retrieval.core.exception.StopException;
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
        ConditionRetry<T> retry = () -> {
            try {
                return (T) pjp.proceed();
            } catch (InterruptedException e) {
                throw new StopException(e);
            } catch (Throwable throwable) {
                throw new ProceedException(throwable);
            }

        };
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