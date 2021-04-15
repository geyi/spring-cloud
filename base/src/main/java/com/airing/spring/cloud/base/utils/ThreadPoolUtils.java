package com.airing.spring.cloud.base.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ThreadPoolUtils {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static volatile ThreadPoolUtils threadPoolUtils = null;
    private volatile ThreadPoolExecutor executor = null;
    private final Object executorLock = new Object();
    private static final AtomicLongFieldUpdater<ThreadPoolUtils> WAITING_TIME_UPDATER =
            AtomicLongFieldUpdater.newUpdater(ThreadPoolUtils.class, "waitingTime");
    private volatile long waitingTime = 0;
    private static final AtomicLongFieldUpdater<ThreadPoolUtils> TOTAL_TIME_UPDATER =
            AtomicLongFieldUpdater.newUpdater(ThreadPoolUtils.class, "totalTime");
    private volatile long totalTime = 0;

    private ThreadPoolUtils() {
    }

    public static ThreadPoolUtils getInstance() {
        if (threadPoolUtils == null) {
            synchronized (ThreadPoolUtils.class) {
                if (threadPoolUtils == null) {
                    threadPoolUtils = new ThreadPoolUtils();
                }
            }
        }
        return threadPoolUtils;
    }

    private void initParam() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize;
        int maxPoolSize;
        if (availableProcessors <= 2) {
            corePoolSize = availableProcessors << 4;
            maxPoolSize = availableProcessors << 5;
        } else {
            corePoolSize = availableProcessors << 2;
            maxPoolSize = availableProcessors << 3;
        }
        initThreadPool(corePoolSize, maxPoolSize, 20000);
    }

    public void initThreadPool(int corePoolSize, int maxPoolSize, int queueSize) {
        if (executor == null) {
            synchronized (executorLock) {
                if (executor == null) {
                    log.info("corePoolSize: {}, maxPoolSize: {}, queueSize: {}", corePoolSize, maxPoolSize, queueSize);
                    executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 200, TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<>(queueSize),
                            new ThreadFactoryBuilder().setNameFormat("agogopost-%d").build(),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
    }

    public void execute(Runnable runnable) {
        if (executor == null) {
            initParam();
        }
        executor.execute(runnable);
        this.print();
    }

    public <T> Future<T> execute(Runnable runnable, T result) {
        if (executor == null) {
            initParam();
        }
        Future<T> future = executor.submit(runnable, result);
        this.print();
        return future;
    }

    public void print() {
        if (executor == null) {
            initParam();
        }
        log.debug("activeCount: {}", executor.getActiveCount());
        log.debug("completedTaskCount: {}", executor.getCompletedTaskCount());
        log.debug("taskCount: {}", executor.getTaskCount());
        log.debug("queueSize: {}", executor.getQueue().size());
        int queueSize = executor.getQueue().size();
        if (queueSize >= 10) {
            log.warn("queueSize: {}", queueSize);
        }
    }

    public int getQueueSize() {
        if (executor == null) {
            initParam();
        }
        return executor.getQueue().size();
    }

    public void updateWaitingTime(long time) {
        long newTime = WAITING_TIME_UPDATER.addAndGet(this, time);
        log.info("new waiting time: {}", newTime);
    }

    public void updateTotalTime(long time) {
        long newTime = TOTAL_TIME_UPDATER.addAndGet(this, time);
        log.info("new total time: {}", newTime);
    }
}
