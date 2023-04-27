package com.airing.spring.cloud.base.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存模板
 * 实现一个基于Map的缓存抽象类，同时支持通过版本控制实现缓存数据的自动更新
 *
 * @author GEYI
 * @date 2021年03月30日 16:42
 */
public abstract class AbstractMapCache2<K, V> {

    private static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    // 指向堆内存内缓存数据的引用变量
    private LoadingCache<K, V> map = CacheBuilder.newBuilder()
            .maximumSize(20000)
            .recordStats()
            .build(new CacheLoader<K, V>() {
                @Override
                public V load(K key) {
                    return AbstractMapCache2.this.load(key);
                }
            });
    // 堆内存内缓存数据的版本
    private AtomicLong version = new AtomicLong(-1);
    /*
    是否使用key的版本控制刷新缓存
    为true时必须重写getVersion(K key)方法
     */
    protected boolean keyVersionCtrl = false;
    // 保存每个key的版本
    private Map<K, Long> keyVersion = new ConcurrentHashMap<>();

    private final Object[] locks = {
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object()
    };
    private int lockCount = locks.length;

    private VersionCtrl<K, V> versionCtrl;

    public LoadingCache<K, V> getMap() {
        return map;
    }

    public AbstractMapCache2() {
        this.versionCtrl = new KeyVersionCtrl();
    }

    public AbstractMapCache2(LoadingCache<K, V> map) {
        this(false, map);
    }

    public AbstractMapCache2(boolean keyVersionCtrl, LoadingCache<K, V> map) {
        this.keyVersionCtrl = keyVersionCtrl;
        this.versionCtrl = keyVersionCtrl ? new KeyVersionCtrl() : new DefVersionCtrl();
        this.map = map;
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

    public V get(K key) {
        return versionCtrl.get(key);
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    private final class KeyVersionCtrl implements VersionCtrl<K, V> {
        @Override
        public V get(K key) {
            long cacheVersion = getVersion(key);
            long currentVersion = keyVersion.getOrDefault(key, -1L);
            if (currentVersion != cacheVersion) {
                synchronized (locks[spread(key.hashCode()) & (lockCount - 1)]) {
                    currentVersion = keyVersion.getOrDefault(key, -1L);
                    if (currentVersion != cacheVersion) {
                        keyVersion.put(key, cacheVersion);
                        map.invalidate(key);
                        try {
                            return map.get(key);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            try {
                return map.get(key);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private final class DefVersionCtrl implements VersionCtrl<K, V> {
        @Override
        public V get(K key) {
            long cacheVersion = getVersion();
            long currentVersion = version.get();
            if (currentVersion != cacheVersion) {
                synchronized (locks[0]) {
                    currentVersion = version.get();
                    if (currentVersion != cacheVersion) {
                        version.compareAndSet(currentVersion, cacheVersion);
                        map.invalidateAll();
                        try {
                            return map.get(key);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            try {
                return map.get(key);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
