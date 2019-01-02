# User guide

## 背景
项目中有很多需要重试的场景，而每次都得写如下的逻辑
```  java
for (int i=0;i++;i<retry){
    try{
        do(//逻辑代码);
        if(success){
          break;
        }
    }catch(Exception e){
    }
}
```

这么做会有如下几个问题

- 每个重试不得不写类似的控制语句，导致代码冗余
- 业务逻辑与控制逻辑耦合
- 重试次数写死，不好动态控制

本组件希望提供一个统一的方案来解决这个简单但给我们代码带来不便的问题。

## 快速入门

> 最简单用法

`new RetryBuilder().build().proceed((round, nanos) -> "success");`

> 外加参数控制

``` java
 new RetryBuilder().retry(10).delay(1000).timeout(5000).pool(Executors.newSingleThreadExecutor()).build().async((round, nanos) -> "success");
```

> 回调形式

``` java
  new RetryBuilder().setConfig(config).build().proceed(
                        new Retryable<Integer>() {
                            @Override
                            public Integer proceed(int round, long nanos) {
                                logger.info("success");
                                return 1;
                            }

                            @Override
                            public Integer whenFinish(Integer ret, int round, long nanos) {
                                logger.info("finish");
                                return 999;
                            }

                            @Override
                            public boolean postCondition(Integer ret, int round, long nanos) {
                                logger.info(round + "");
                                return true;
                            }

                            @Override
                            public boolean preCondition(int round, long nanos) {
                                return true;
                            }
                        }));
```

> 无回调形式
``` java
        Class[] failOn = {IllegalAccessException.class};
        Class[] continueOn = {IllegalArgumentException.class};
        new RetryBuilder()
                .withCondition()
                .failOn(failOn)
                .continueOn(continueOn)
                .build()
                .proceed(() -> "success");
```


## RetryBuilder 设置参数说明

> 设置

`retry` 即重试次数（默认：1）

`delay` 每次重试间隔 （默认：0）

`timeout` 执行超时时间(proceed模式不支持) （默认：FOREVER）

`pool` 自定义线程池 （默认：全局唯一的CachedThreadPool）

`timeoutPolice` 超时时所采取的停止策略。SetFlag：只设置标识位，重试线程必须跑完一次循环才会跳出。InterruptAndSetFlag：触发中断并设置标识位。（默认：InterruptAndSetFlag）

> 回调（callback形式下生效）

`whenError` 重试体抛异常时回调，并返回是否需要跳出循环

`whenFinish` 整个重试逻辑正常执行完毕时回调

`postCondition` 循环后置条件，每次重试完成时回调

`preCondition` 循环前置条件，每次执行前回调

`whenTimeout` 重试超时时回调，此时不会再回调`whenFinish`

> 条件（condition形式下生效）

`withCondition` 进入condition形式

`failOn` 循环失败条件

`continueOn` 循环继续条件


## [二种形式 callback|condition](doc/两种形式.md)



## [三种执行模式 proceed|sync|async](doc/三种模式.md)
 


 
## [与spring整合](doc/与spring整合.md)




## 后期版本计划

- 整合spring，注解化运行（已完成）
- 区分service进行熔断降级（计划中）
- 支持dashboard，显示重试失败情况（待）
