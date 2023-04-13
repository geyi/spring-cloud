package com.airing.spring.cloud.base.cache;

import com.airing.spring.cloud.base.ApplicationTests;
import com.airing.spring.cloud.base.cache.demo.DefaultPriceCache;
import com.airing.spring.cloud.base.cache.demo.DefaultPriceCacheKey;
import com.airing.spring.cloud.base.cache.demo.PriceCacheKey;
import com.airing.spring.cloud.base.cache.demo.PriceCacheValue;
import com.airing.spring.cloud.base.cache.demo.PriceDetailCache;
import com.airing.spring.cloud.base.cache.demo.PriceL1Cache;
import com.airing.spring.cloud.base.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@Slf4j
public class CacheTest extends ApplicationTests {
    @Autowired
    private RedissonUtils redissonUtils;
    @Autowired
    private DefaultPriceCache defaultPriceCache;
    @Autowired
    private PriceL1Cache priceL1Cache;

    @Test
    public void priceDetailCacheTest() {
        PriceDetailCache priceDetailCache = new PriceDetailCache(10, redissonUtils, 1L);

        PriceCacheKey key = new PriceCacheKey();
        key.setPriceMetaId(1L);
        key.setXzqRelationId(1);
        key.setBizType("STANDARD");

        redissonUtils.del(String.format(PriceDetailCache.PRICE_VERSION, 1L));
        key.setCacheVersion(0);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);
        key.setCacheVersion(1);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);

        /**
         * 阈值为10时
         * 1次from DB
         * 10次from Redis
         * 89次from JVM
         */
        for (int i = 0; i < 100; i++) {
            priceDetailCache.getValue(key);
        }

        priceDetailCache.remove(key);

        priceDetailCache.getValue(key);
    }

    @Test
    public void multiThreadPriceDetailCacheTest() throws InterruptedException {
        PriceDetailCache priceDetailCache = new PriceDetailCache(10, redissonUtils, 1L);

        PriceCacheKey key = new PriceCacheKey();
        key.setPriceMetaId(1L);
        key.setXzqRelationId(1);
        key.setBizType("STANDARD");

        redissonUtils.del(String.format(PriceDetailCache.PRICE_VERSION, 1L));
        key.setCacheVersion(0);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);
        key.setCacheVersion(1);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);

        // 只会输出一次from DB
        int count = 10;
        Thread[] threads = new Thread[count];
        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(() -> {
                /*try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                for (int j = 0; j < 1; j++) {
                    priceDetailCache.getValue(key);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        priceDetailCache.getValue(key);
        priceDetailCache.getValue(key);
    }

    @Test
    public void multiThreadPriceDetailCacheTest2() throws InterruptedException {
        PriceDetailCache priceDetailCache = new PriceDetailCache(100, redissonUtils, 1L);

        PriceCacheKey key = new PriceCacheKey();
        key.setPriceMetaId(1L);
        key.setXzqRelationId(1);
        key.setBizType("STANDARD");

        redissonUtils.del(String.format(PriceDetailCache.PRICE_VERSION, 1L));
        key.setCacheVersion(0);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);
        key.setCacheVersion(1);
        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);

        // 只会输出一次from DB
        int count = 100;
        Thread[] threads = new Thread[count];
        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(() -> {
                /*try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                priceDetailCache.getValue(key);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        priceDetailCache.changeVersion();

        // 只会输出一次from DB
        threads = new Thread[count];
        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(() -> {
                /*try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                priceDetailCache.getValue(key);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        priceDetailCache.getValue(key);
        priceDetailCache.getValue(key);
    }

    @Test
    public void reallyTest() {
        PriceCacheKey key = new PriceCacheKey();
        key.setPriceMetaId(1L);
        key.setXzqRelationId(1);
        key.setBizType("STANDARD");
        log.info(priceL1Cache.get(1L).getValue(key).toString());
        log.info(priceL1Cache.get(1L).getValue(key).toString());
        log.info(priceL1Cache.get(1L).getValue(key).toString());
        priceL1Cache.changeVersion(1L);
        log.info(priceL1Cache.get(1L).getValue(key).toString());
    }

//    @Test
    public void defaultPriceCacheTest() {
        DefaultPriceCacheKey key = new DefaultPriceCacheKey();
        key.setChannel("PERSONAL");
        key.setExpressCode("shunfeng");

        redissonUtils.del(key.uniqueKey());
        redissonUtils.del(key.uniqueKey() + AbstractRedisCache.COUNT_SUFFIX);

        for (int i = 0; i < 100; i++) {
            defaultPriceCache.getValue(key);
        }

        defaultPriceCache.remove(key);

        defaultPriceCache.getValue(key);
    }
}
