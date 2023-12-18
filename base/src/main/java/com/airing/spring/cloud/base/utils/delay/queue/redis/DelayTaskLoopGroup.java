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

    /**
     * 关闭组内的所有线程
     *
     * @param timeout 等待所有线程关闭的超时时间
     * @return boolean 在指定的超时时间内如果所有线程都关闭则返回true，否则返回false
     * @author GEYI
     * @date 2023年12月18日 14:07
     */
    public boolean shutdown(long timeout) {
        for (Map.Entry<DelayTaskLoop, Thread> es : threadMap.entrySet()) {
            DelayTaskLoop loop = es.getKey();
            loop.shutdown();
        }

        boolean alive = false;
        long future = System.currentTimeMillis() + timeout;
        long remaining;
        do {
            alive = false;
            for (Map.Entry<DelayTaskLoop, Thread> es : threadMap.entrySet()) {
                Thread thread = es.getValue();
                if (thread.isAlive()) {
                    alive = true;
                    break;
                }
            }
            remaining = future - System.currentTimeMillis();
        } while (alive && remaining > 0);
        return !alive;
    }
}
