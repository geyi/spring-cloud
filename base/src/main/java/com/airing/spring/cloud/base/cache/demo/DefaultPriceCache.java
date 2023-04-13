package com.airing.spring.cloud.base.cache.demo;

import com.airing.spring.cloud.base.cache.AbstractRedisCache;
import com.airing.spring.cloud.base.utils.RedissonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DefaultPriceCache extends AbstractRedisCache<DefaultPriceCacheKey, String> {

    @Autowired
    private RedissonUtils redissonUtils;

    public DefaultPriceCache() {
        super(10);
    }

    @Override
    public String load(DefaultPriceCacheKey key) {
        return "SUCCESS";
    }

    @Override
    public void set(String key, String val) {
        redissonUtils.set(key, val, 1, TimeUnit.DAYS);
    }

    @Override
    public long incr(String key, long val) {
        return redissonUtils.incr(key, val, 1, TimeUnit.DAYS);
    }

    @Override
    public String getValue(String key) {
        return redissonUtils.getValue(key);
    }

    @Override
    public boolean del(String key) {
        return redissonUtils.del(key);
    }
}
