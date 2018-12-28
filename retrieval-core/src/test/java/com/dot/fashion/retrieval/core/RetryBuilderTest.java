package retrieval.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * title:
 * author:吉
 * since:2018/12/21
 */
public class RetryBuilderTest {

    @Test
    public void buildTest() {
        Assert.assertEquals(new RetryBuilder().getRetryConfig(), new RetryBuilder().getRetryConfig());
    }
}