package com.dot.fashion;

/**
 * title:重试任务
 * author:吉
 * since:2018/12/19
 */
@FunctionalInterface
public interface Retry<T> {

    T proceed() throws Exception;

    /**
     * proceed 报错时回调
     *
     * @param e     ex
     * @param round 执行次数
     * @param nanos 消耗时间
     */
    default void whenError(Throwable e, int round, long nanos) {

    }

    /**
     * 执行结束时，返回结果处理
     *
     * @param ret proceed执行结果
     * @return 处理后结果
     */
    default T whenFinish(T ret) {
        return ret;
    }

    /**
     * 循环后置条件
     *
     * @param ret proceed执行结果
     * @return 是否需要继续重试
     */
    default boolean postCondition(T ret, int round, long nanos) {
        return true;
    }


    /**
     * 循环前置条件
     *
     * @param round 执行次数
     * @return 是否可执行
     */
    default boolean preCondition(int round) {
        return true;
    }

    /**
     * 超时
     */
    default void whenTimeout() {

    }

}
