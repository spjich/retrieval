package com.dot.fashion;

/**
 * title:重试任务
 * author:吉
 * since:2018/12/19
 */
@FunctionalInterface
public interface Retry<T> {

    T proceed() throws InterruptedException;

    /**
     * proceed 报错时回调
     *
     * @param e 异常
     */
    default void whenError(Throwable e) {

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
     * 判断proceed结果是否符合预期
     *
     * @param ret proceed执行结果
     * @return 是否需要继续重试
     */
    default boolean canOutBreak(T ret, int round, long nanos) {
        return true;
    }

    /**
     * 超时
     */
    default void whenTimeout() {

    }

}
