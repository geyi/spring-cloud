package com.airing.spring.cloud.base.cache.demo;

import com.airing.spring.cloud.base.cache.AbstractRedisCache;
import com.airing.spring.cloud.base.utils.RedissonUtils;

import java.util.concurrent.TimeUnit;

public class PriceDetailCache extends AbstractRedisCache<PriceCacheKey, PriceCacheValue> {
    public static final String PRICE_VERSION = "PRICE_VERSION_%d";

    private RedissonUtils redissonUtils;
    private long priceMetaId;

    public PriceDetailCache(RedissonUtils redissonUtils, long priceMetaId) {
        this(100, redissonUtils, priceMetaId);
    }

    public PriceDetailCache(int threshold, RedissonUtils redissonUtils, long priceMetaId) {
        super(threshold, 1000);
        this.redissonUtils = redissonUtils;
        this.priceMetaId = priceMetaId;
    }

    @Override
    public PriceCacheValue load(PriceCacheKey key) {
        /**
         * todo
         * xzq relation id 转 收寄件地址和xzqLevel
         * 然后查询DB
         */
        // 模拟查询数据库耗时
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PriceCacheValue("FIRST_OVER", "[{\"fw\":\"0\",\"o\":false,\"p\":17.1,\"tw\":\"1\"},{\"fw\":\"\"," +
                "\"o\":true,\"p\":5.7,\"tw\":\"\"}]");
    }

    @Override
    public void set(String key, PriceCacheValue val) {
        redissonUtils.set(key, val, 1, TimeUnit.DAYS);
    }

    @Override
    public long incr(String key, long val) {
        return redissonUtils.incr(key, val, 1, TimeUnit.DAYS);
    }

    @Override
    public PriceCacheValue getValue(String key) {
        return redissonUtils.getValue(key);
    }

    @Override
    public boolean del(String key) {
        return redissonUtils.del(key);
    }

    @Override
    public long getVersion() {
        return redissonUtils.getIncrVal(String.format(PRICE_VERSION, priceMetaId));
    }

    public long changeVersion() {
        return redissonUtils.incr(String.format(PRICE_VERSION, priceMetaId), 1, TimeUnit.DAYS);
    }
}
