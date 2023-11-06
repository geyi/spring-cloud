package com.airing.spring.cloud.base.utils.delay.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
public class DelayTask<T, R> {
    // 周期数
    private Integer cycleNum;
    // 任务处理方法
    private Function<T, R> function;
    private T t;
    private R r;
}
