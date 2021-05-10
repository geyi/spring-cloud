package com.airing.spring.cloud.base.utils;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@ConditionalOnClass({Redisson.class, RedisOperations.class})
@ConditionalOnMissingBean(name = "redisTemplate")
@Component
public class RedissonUtils {

    @Autowired
    private RedissonClient redissonClient;

    public String get(String key) {
        RBucket rb = redissonClient.getBucket(key);
        Object obj = rb.get();
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public <V> V getValue(String key) {
        RBucket<V> rb = redissonClient.getBucket(key);
        return rb.get();
    }

    public String get(String key, Codec codec) {
        RBucket rb = redissonClient.getBucket(key, codec);
        Object obj = rb.get();
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public void set(String key, String value, long expireTime, TimeUnit timeUnit) {
        RBucket rb = redissonClient.getBucket(key);
        if (expireTime < 0) {
            rb.set(value);
        } else {
            rb.set(value, expireTime, timeUnit);
        }
    }

    public <V> void set(String key, V value, long expireTime, TimeUnit timeUnit) {
        RBucket rb = redissonClient.getBucket(key);
        if (expireTime < 0) {
            rb.set(value);
        } else {
            rb.set(value, expireTime, timeUnit);
        }
    }

    public void set(String key, String value, long expireTime, TimeUnit timeUnit, Codec codec) {
        RBucket rb = redissonClient.getBucket(key, codec);
        if (expireTime < 0) {
            rb.set(value);
        } else {
            rb.set(value, expireTime, timeUnit);
        }
    }

    public void set(String key, String value) {
        set(key, value, -1, TimeUnit.MILLISECONDS);
    }

    public <V> void set(String key, V value) {
        set(key, value, -1, TimeUnit.MILLISECONDS);
    }

    public boolean del(String key) {
        RBucket rb = redissonClient.getBucket(key);
        return rb.delete();
    }

    public boolean setnx(String key, String value) {
        RBucket rb = redissonClient.getBucket(key);
        return rb.trySet(value);
    }

    public boolean setnx(String key, String value, long expireTime, TimeUnit timeUnit) {
        RBucket rb = redissonClient.getBucket(key);
        return rb.trySet(value, expireTime, timeUnit);
    }

    /**
     * 获取锁
     *
     * @param key
     * @param waitSeconds 等待获取锁的阻塞时间，单位：秒
     * @return
     */
    public boolean lock(String key, long waitSeconds) {
        RLock lock = redissonClient.getLock(key);
        try {
            // 60s后强制释放锁
            boolean result = lock.tryLock(waitSeconds, 60L, TimeUnit.SECONDS);
//            log.info("lock|END|key={},waitSeconds={}s|{}", key, waitSeconds, result);
            return result;
        } catch (InterruptedException e) {
//            log.error("lock|ERROR|key={},waitSeconds={}s", key, waitSeconds);
            return false;
        }
    }

    public long incr(String key) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        return rAtomicLong.incrementAndGet();
    }

    public long incr(String key, long expireTime, TimeUnit timeUnit) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        long ret = rAtomicLong.incrementAndGet();
        rAtomicLong.expire(expireTime, timeUnit);
        return ret;
    }

    public void incr(String key, long value, long expireTime, TimeUnit timeUnit) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        rAtomicLong.set(value);
        rAtomicLong.expire(expireTime, timeUnit);
    }

    public long addAndGet(String key, long value, long expireTime, TimeUnit timeUnit) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        long ret = rAtomicLong.addAndGet(value);
        rAtomicLong.expire(expireTime, timeUnit);
        return ret;
    }

    /**
     * key不存在时返回0
     *
     * @param key
     * @return
     */
    public long getIncrVal(String key) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        return rAtomicLong.get();
    }

    public void unlock(String key) {
        redissonClient.getLock(key).unlock();
//        log.info("unlock|END|key={}", key);
    }

    public boolean sRem(String key, String value) {
        RSet rSet = redissonClient.getSet(key, new StringCodec());
        return rSet.remove(value);
    }

    public boolean sRem(String key, Object value) {
        RSet rSet = redissonClient.getSet(key, new StringCodec());
        return rSet.remove(value);
    }

    public Set<String> sMembers(String key) {
        RSet rSet = redissonClient.getSet(key, new StringCodec());
        return rSet.readAll();
    }

    public <V> Set<V> sMembersV(String key) {
        RSet<V> rSet = redissonClient.getSet(key, new StringCodec());
        return rSet.readAll();
    }

    public boolean isExists(String key) {
        RBucket rb = redissonClient.getBucket(key);
        return rb.isExists();
    }

    public boolean bitSet(String key, long bitIndex) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        return bitSet.set(bitIndex);
    }

    public boolean bitGet(String key, long bitIndex) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        return bitSet.get(bitIndex);
    }
}
