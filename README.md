# User guide

## 背景
项目中有很多需要重试的场景，而每次都得写如下的逻辑
```  java
for (int i=0;i++;i<num){
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

## 基本用法

### proceed模式

业务线程与重试线程相同
单线程运行，并且尝试2次（注：proceed模式下不支持timeout设置）


``` java
new RetryBuilder().build().proceed(() -> "success");
```

### sync模式

业务线程与重试线程不同
异步阻塞模式,重试+超时时间设定
``` java
new RetryBuilder().num(10).timeout(5000).build().sync(() -> "success");
```
重试10次，且5s后无论成功与否都会返回结果


### async模式

业务线程与重试线程不同
异步非阻塞模式，重试+超时时间设定+自定义线程池
``` java
new RetryBuilder().retry(10).delay(1000).timeout(5000).pool(Executors.newSingleThreadExecutor()).build().async((round, nanos) -> "success");
```
调用线程非阻塞，重试会在线程池中继续运行，到达超时时间后自动停止

## 核心参数解释

retry: 即重试次数

delay: 每次重试间隔

timeout：执行超时时间(proceed模式不支持)

pool：自定义线程池



## 回调支持

`whenError` 重试体抛异常时回调，并返回是否需要跳出循环

`whenFinish` 整个重试逻辑正常执行完毕时回调

`postCondition` 循环后置条件，每次重试完成时回调

`preCondition` 循环前置条件，每次执行前回调

`whenTimeout` 重试超时时回调，此时不会再回调`whenFinish`

## 后期版本计划

- 整合spring，注解化运行
- 接入apollo，disconf，支持动态修改重试配置
- 支持dashboard，显示重试失败情况
- 支持脚本化，重试逻辑热加载





