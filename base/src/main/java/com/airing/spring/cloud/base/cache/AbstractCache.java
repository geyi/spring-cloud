package com.airing.spring.cloud.base.cache;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存模板
 *
 * @author GEYI
 * @date 2021年03月30日 16:42
 */
public abstract class AbstractCache<T> {

    // 指向堆内存内缓存数据的引用变量
    private List<T> list = null;
    // 堆内存内缓存数据的版本
    private AtomicLong version = new AtomicLong(-1);
    // 同步锁
    private final Object lock = new Object();
    // 默认阻塞模式，非阻塞模式存在返回null或者旧数据的情况
    protected boolean blocking = true;

    /**
     * 获取最新的数据版本
     *
     * @return long
     * @author GEYI
     * @date 2021年03月30日 16:42
     */
    public abstract long getVersion();

    /**
     * 加载最新的数据
     *
     * @return java.util.List<T>
     * @author GEYI
     * @date 2021年03月30日 16:42
     */
    public abstract List<T> load();

    private void cacheList() {
        List<T> dataList = this.load();
        if (dataList == null) {
//            log.error("data list is null");
        } else {
            list = dataList;
        }
    }

    public List<T> getList() {
        long cacheVersion = this.getVersion();
        long currentVersion = version.get();
        if (currentVersion != cacheVersion || currentVersion == -1 || list == null) {
            if (blocking) {
                synchronized (lock) {
                    if (currentVersion != cacheVersion || currentVersion == -1 || list == null) {
//                        log.info("blocking reload");
                        this.cacheList();
                        version.set(cacheVersion);
                    }
                }
            } else {
                if (version.compareAndSet(currentVersion, cacheVersion)) {
//                    log.info("non blocking reload");
                    this.cacheList();
                }
            }
        }
        return list;
    }

}
