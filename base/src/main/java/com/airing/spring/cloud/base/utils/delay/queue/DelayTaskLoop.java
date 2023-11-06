package com.airing.spring.cloud.base.utils.delay.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Slf4j
public class DelayTaskLoop implements Runnable {
    private static final int SLOT_LEN = 3600;

    private final Lock lock = new ReentrantLock();

    // 槽位
    private Slot[] slots = new Slot[SLOT_LEN];
    // 跳转到下一个槽的间隔时间
    private long interval = 1;

    public DelayTaskLoop() {
        this(1);
    }

    public DelayTaskLoop(int interval) {
        for (int i = 0; i < SLOT_LEN; i++) {
            slots[i] = new Slot(i);
        }
        this.interval = interval;
    }

    /**
     * TODO 有bug！！！
     *
     * @param function
     * @param delayTime 执行任务的延迟时间，单位秒
     * @author GEYI
     * @date 2023年08月03日 21:08
     */
    public void put(Function function, int delayTime) {
        lock.lock();
        try {
            int cycle;
            int idx;
            if (delayTime < SLOT_LEN) {
                cycle = 0;
                idx = delayTime % SLOT_LEN - 1;
            } else {
                cycle = delayTime / SLOT_LEN;
                idx = delayTime - (SLOT_LEN * cycle);
                idx = idx == 0 ? 0 : idx - 1;
            }

            DelayTask delayTask = new DelayTask<>();
            delayTask.setCycleNum(cycle);
            delayTask.setFunction(function);

            slots[idx].getTaskSet().add(delayTask);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            for (Slot slot : slots) {
                sleep(interval);

                for (DelayTask delayTask : slot.getTaskSet()) {
                    Integer cycleNum = delayTask.getCycleNum();
                    if (cycleNum == 0) {
                        // 使用线程池进行处理
                        Thread t = new Thread(() -> {
                            Object ret = delayTask.getFunction().apply(delayTask.getT());
                        });
                        t.start();
                    } else {
                        delayTask.setCycleNum(cycleNum - 1);
                    }
                }

                System.out.println(slot.getNumber());
            }
        }
    }

    private void sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            log.error("sleep err");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DelayTaskLoop delayTaskLoop = new DelayTaskLoop();
        Thread t = new Thread(delayTaskLoop);
        t.start();

        int i = 10;
        Thread[] threads = new Thread[i];
        for (int j = 0; j < i; j++) {
            int finalJ = j;
            threads[j] = new Thread(() -> {
                delayTaskLoop.put(o -> {
                    System.out.println("hello");
                    return null;
                }, finalJ + 10);
                System.out.println("put task");
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }

        t.join();
    }
}
