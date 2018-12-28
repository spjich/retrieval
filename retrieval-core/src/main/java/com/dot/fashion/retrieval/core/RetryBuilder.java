package retrieval.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * title:构造器
 * author:吉
 * since:2018/12/19
 */
@SuppressWarnings("WeakerAccess")
public class RetryBuilder {
    public static final int FOREVER = -1;
    private RetryConfig retryConfig;
    private static final RetryConfig Default = new RetryConfig(1, -1, Executors.newCachedThreadPool(), 0);


    public RetryBuilder() {
        this.retryConfig = Default;
    }

    public RetryBuilder setConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        return this;
    }

    /**
     * 设置重试次数
     *
     * @param num
     * @return
     */
    public RetryBuilder retry(int num) {
        retryConfig.setNum(num);
        return this;
    }

    /**
     * 设置线程池
     *
     * @param pool
     * @return
     */
    public RetryBuilder pool(ExecutorService pool) {
        retryConfig.setExecutorService(pool);
        return this;
    }

    /**
     * 重试间隔设置
     *
     * @param mills
     * @return
     */
    public RetryBuilder delay(long mills) {
        retryConfig.setTimeLimitMilli(mills);
        return this;
    }

    /**
     * 设置超时时间
     * 仅在非proceed模式下生效
     *
     * @param millSeconds
     * @return
     */
    public RetryBuilder timeout(long millSeconds) {
        retryConfig.setTimeLimitMilli(millSeconds);
        return this;
    }

    protected RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public RetryLoop build() {
        return new RetryLoop(retryConfig);
    }
}
