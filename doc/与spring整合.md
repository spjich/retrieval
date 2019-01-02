#与spring整合

``` java
@Retrieval(retry = 3,
            delayMilli = 1000,
            module = RetryModule.ASYNC,
            timeLimitMilli = 15000,
            failOn = IllegalArgumentException.class,
            continueOn = IllegalAccessException.class)
    public String test(int a) {
        return "success";
    }
```

- 使用condition形式方式与spring整合
- 注解使用在方法上，该方法作为重试目标

