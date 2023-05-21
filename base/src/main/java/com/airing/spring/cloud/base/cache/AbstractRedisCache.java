package com.airing.spring.cloud.base.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓存模板
 *
 * @author GEYI
 * @date 2021年03月30日 16:42
 */
@Slf4j
public abstract class AbstractRedisCache<K extends RedisKey, V> {

    public static final String COUNT_SUFFIX = "_COUNT";
    public static final Object EMPTY = new Object();
    private static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    // 访问量阈值，默认100
    private int threshold;
    // 指向堆内存内缓存数据的引用变量
    // private Map<K, V> cache = new ConcurrentHashMap<>();
    private LoadingCache<K, V> cache;
    // 堆内存内缓存数据的版本
    private AtomicLong version = new AtomicLong(-1);
    // 锁
    private ReentrantLock[] locks = new ReentrantLock[16];
    private int lockCount = locks.length;
    private int lockSlotCount = lockCount - 1;

    public AbstractRedisCache() {
        this(100, 1000);
    }

    public AbstractRedisCache(int threshold, int maximumSize) {
        this.threshold = threshold;
        for (int i = 0; i < lockCount; i++) {
            locks[i] = new ReentrantLock();
        }

        cache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) {
                        return (V) AbstractRedisCache.EMPTY;
                    }
                });
    }

    /**
     * 加载最新数据
     *
     * @param key
     * @return V
     * @author GEYI
     * @date 2023年04月12日 19:40
     */
    public abstract V load(K key);

    /**
     * 向Redis中保存kv
     *
     * @param key
     * @param val
     * @author GEYI
     * @date 2023年04月12日 19:41
     */
    public abstract void set(String key, V val);

    /**
     * 给key增加给定的数量
     * 
     * @param key
     * @return long
     * @author GEYI
     * @date 2023年04月12日 19:44
     */
    public abstract long incr(String key, long val);

    /**
     * 从Redis中获取key的值
     *
     * @param key
     * @return V
     * @author GEYI
     * @date 2023年04月12日 19:52
     */
    public abstract V getValue(String key);

    /**
     * 从Redis中删除给定key
     *
     * @param key
     * @return boolean
     * @author GEYI
     * @date 2023年04月12日 19:52
     */
    public boolean del(String key) {
        return false;
    }

    /**
     * 你可以重写getVersion方法返回一个递增的版本号来控制缓存自动刷新，
     * 这种方式可以保证数据的最终一致性。
     * 否则你需要在数据更新时调用{@link #remove(K) remove(K)}方法清除旧的缓存数据，
     * 这种方式在高并发的读写场景下可能会出现数据一致性问题。
     *
     * @return long
     * @author GEYI
     * @date 2023年04月13日 15:58
     */
    public long getVersion() {
        return -1;
    }

    private V cache(K key) {
        if (log.isDebugEnabled()) {
            log.debug("from DB");
        }
        V v = this.load(key);
        if (v != null) {
            set(key.uniqueKey(), v);
        }
        return v;
    }

    private long incrAccessCount(K key, V v) {
        String k = key.getKeyWithSuffix(COUNT_SUFFIX);
        long accessCount = incr(k, 1L);
        if (accessCount >= threshold) {
            cache.put(key, v);
        }
        return accessCount;
    }

    public V getValue(K key) {
        long cacheVersion = getVersion();
        if (cacheVersion != -1) {
            if (log.isDebugEnabled()) {
                log.debug("version control");
            }
            long currentVersion = version.get();
            if (currentVersion != cacheVersion) {
                ReentrantLock lock = locks[spread(key.hashCode()) & lockSlotCount];
                lock.lock();
                try {
                    currentVersion = version.get();
                    if (currentVersion != cacheVersion) {
                        // 使用版本号控制缓存刷新
                        key.setCacheVersion((int) cacheVersion);

                        version.compareAndSet(currentVersion, cacheVersion);
                        clearMap();
                        return cache(key);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        V v;
        try {
            v = cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (v != null && v != EMPTY) {
            if (log.isDebugEnabled()) {
                log.debug("from JVM cache");
            }
            return v;
        }

        v = getValue(key.uniqueKey());
        if (v != null) {
            if (log.isDebugEnabled()) {
                log.debug("from Redis cache");
            }
            incrAccessCount(key, v);
        } else {
            ReentrantLock lock = locks[spread(key.hashCode()) & lockSlotCount];
            lock.lock();
            try {
                v = getValue(key.uniqueKey());
                if (v == null) {
                    v = cache(key);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("get lock and from Redis cache");
                    }
                    incrAccessCount(key, v);
                }
            } finally {
                lock.unlock();
            }
        }
        return v;
    }

    public void remove(K key) {
        cache.invalidate(key);
        del(key.uniqueKey());
    }

    public void clearMap() {
        cache.invalidateAll();
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

}
