package com.airing.spring.cloud.base.utils.delay.queue.redis;

import com.airing.spring.cloud.base.utils.RedissonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * redis延迟任务处理器
 *
 * @author GEYI
 * @date 2022年05月16日 9:15
 */
@Slf4j
public class DelayTaskLoop<T> implements Runnable {

    private String queueName;
    private RedissonUtils redissonUtils;
    private long interval;
    // 记录当前队列中task的数量
    private AtomicLong count = new AtomicLong(0);
    private volatile boolean shutdown = false;

    public String getQueueName() {
        return queueName;
    }

    public long getCount() {
        return count.get();
    }

    public DelayTaskLoop(String queueName, RedissonUtils redissonUtils) {
        this(queueName, redissonUtils, 30000);
    }

    public DelayTaskLoop(String queueName, RedissonUtils redissonUtils, long interval) {
        this.queueName = queueName;
        this.redissonUtils = redissonUtils;
        this.interval = interval;
    }

    /**
     * 向redis添加延迟任务
     *
     * @param task
     * @param delayTime
     * @param timeUnit
     * @return boolean
     * @author GEYI
     * @date 2023年08月25日 17:13
     */
    public boolean offerTask(T task, long delayTime, TimeUnit timeUnit) {
        try {
            redissonUtils.delayQueueOffer(queueName, task, delayTime, timeUnit);
            long total = count.incrementAndGet();
            if (log.isDebugEnabled()) {
                log.debug("延迟任务加入成功|{}|{}|{}|{}|{}", Thread.currentThread().getName(), JSONObject.toJSONString(task),
                        delayTime, timeUnit.toString(), total);
            }
            return true;
        } catch (Exception e) {
            log.error("延迟任务加入失败|{}|{}|{}|{}", Thread.currentThread().getName(), JSONObject.toJSONString(task),
                    delayTime, timeUnit.toString());
        }
        return false;
    }

    /**
     * 任务处理
     *
     * @author GEYI
     * @date 2022年05月16日 9:20
     */
    @Override
    public void run() {
        RBlockingQueue<T> delayQueue = redissonUtils.getDelayQueue(queueName);
        T task = null;
        while (true) {
            if (shutdown) {
                break;
            }
            try {
                task = delayQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (task == null) {
                    continue;
                }
                long total = count.decrementAndGet();
                handler(task);
                if (log.isDebugEnabled()) {
                    log.debug("延迟任务处理完成|{}|{}|{}", Thread.currentThread().getName(), JSONObject.toJSONString(task),
                            total);
                }
            } catch (Exception e) {
                log.error("延时任务处理异常|{}|{}", JSONObject.toJSONString(task), e.getMessage(), e);
                return;
            }
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    /**
     * 通常由子类实现具体的处理过程
     *
     * @param task
     * @author GEYI
     * @date 2022年05月16日 9:20
     */
    public void handler(T task) {
        log.info("handler|start|task:{}", task.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayTaskLoop that = (DelayTaskLoop) o;
        return queueName.equals(that.queueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queueName);
    }
}
