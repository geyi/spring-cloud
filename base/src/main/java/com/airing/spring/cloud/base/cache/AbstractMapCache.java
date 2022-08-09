package com.airing.spring.cloud.base.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存模板
 *
 * @author GEYI
 * @date 2021年03月30日 16:42
 */
public abstract class AbstractMapCache<K, V> {

    // 指向堆内存内缓存数据的引用变量
    private Map<K, V> map = new ConcurrentHashMap<>();
    // 堆内存内缓存数据的版本
    private AtomicLong version = new AtomicLong(-1);
    // 同步锁
    private final Object lock = new Object();
    /*
    是否使用key的版本控制刷新缓存
    为true时必须重写getVersion(K key)方法
     */
    protected boolean keyVersionCtrl = false;
    // 保存每个key的版本
    private Map<K, Long> keyVersion = new ConcurrentHashMap<>();

    public AbstractMapCache() {
    }

    public AbstractMapCache(boolean keyVersionCtrl) {
        this.keyVersionCtrl = keyVersionCtrl;
    }

    /**
     * 获取最新的数据版本
     *
     * @return long
     * @author GEYI
     * @date 2021年03月30日 16:42
     */
    public abstract long getVersion();

    public long getVersion(K key) {
        return 0;
    }

    public abstract V load(K key);

    private void cacheMap(K key) {
        V v = this.load(key);
        if (v != null) {
            map.put(key, v);
        }
    }

    public V get(K key) {
        long cacheVersion;
        long currentVersion;
        if (keyVersionCtrl) {
            cacheVersion = this.getVersion(key);
            currentVersion = keyVersion.getOrDefault(key, -1L);
        } else {
            cacheVersion = this.getVersion();
            currentVersion = version.get();
        }
        if (currentVersion != cacheVersion) {
            synchronized (lock) {
                currentVersion = keyVersionCtrl ? keyVersion.getOrDefault(key, -1L) : version.get();
                if (currentVersion != cacheVersion) {
                    if (keyVersionCtrl) {
                        keyVersion.put(key, cacheVersion);
                        remove(key);
                    } else {
                        version.compareAndSet(currentVersion, cacheVersion);
                        map.clear();
                    }
                    this.cacheMap(key);
                }
            }
        }
        V v;
        if ((v = map.get(key)) == null) {
            synchronized (lock) {
                if ((v = map.get(key)) == null) {
                    this.cacheMap(key);
                }
            }
        }
        return v;
    }

    public V remove(K key) {
        return map.remove(key);
    }

}
