package com.airing.spring.cloud.base.utils.delay.queue.redis;

import com.airing.spring.cloud.base.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
public class DelayTaskLoopGroup<T> {
    private int size;
    private String queueNamePrefix;
    private DelayTaskLoop[] loops;
    private final AtomicLong idx = new AtomicLong();
    private Map<DelayTaskLoop, Thread> threadMap = new HashMap<>();

    public DelayTaskLoopGroup(int size, String queueNamePrefix, RedissonUtils redissonUtils, Consumer<T> consumer) {
        this.size = size;
        this.queueNamePrefix = queueNamePrefix;
        init(queueNamePrefix, redissonUtils, consumer);
    }

    private void init(String queueNamePrefix, RedissonUtils redissonUtils, Consumer<T> consumer) {
        if (size <= 0) {
            throw new RuntimeException("size must gt 0");
        }
        loops = new DelayTaskLoop[size];
        for (int i = 0; i < size; i++) {
            loops[i] = new DelayTaskLoop<T>(queueNamePrefix + "_" + i, redissonUtils) {
                @Override
                public void handler(T task) {
                    consumer.accept(task);
                }
            };
        }
        int i = 0;
        for (DelayTaskLoop loop : loops) {
            Thread thread = new Thread(loop, queueNamePrefix + "-" + i++);
            thread.start();
            threadMap.put(loop, thread);
        }
    }

    public DelayTaskLoop chooseLoop() {
        return loops[(int) Math.abs(idx.getAndIncrement() % size)];
    }

    public void healthCheck() {
        int normal = 0;
        int exp = 0;
        int reboot = 0;
        for (Map.Entry<DelayTaskLoop, Thread> es : threadMap.entrySet()) {
            Thread thread = es.getValue();
            DelayTaskLoop loop = es.getKey();
            if (thread == null || !thread.isAlive()) {
                String queueName = loop.getQueueName();
                log.warn("{} is not alive", queueName);
                thread = new Thread(loop, queueName);
                thread.start();
                threadMap.put(loop, thread);
                exp++;
                reboot++;
            } else {
                normal++;
                log.info("{}延迟队列正常|{}", loop.getQueueName(), loop.getCount());
            }
        }
        log.info("{} health stat: normal {}, exp {}, reboot {}", queueNamePrefix, normal, exp, reboot);
    }
}
