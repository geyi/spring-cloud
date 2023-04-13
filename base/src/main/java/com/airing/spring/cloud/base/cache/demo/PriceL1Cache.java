package com.airing.spring.cloud.base.cache.demo;

import com.airing.spring.cloud.base.cache.AbstractMapCache;
import com.airing.spring.cloud.base.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PriceL1Cache extends AbstractMapCache<Long, PriceDetailCache> {

    public PriceL1Cache() {
        super(true);
        log.info("PriceL1Cache keyVersionCtrl|{}", super.keyVersionCtrl);
    }

    @Autowired
    private RedissonUtils redissonUtils;

    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public long getVersion(Long key) {
        return 0;
    }

    @Override
    public PriceDetailCache load(Long key) {
        return new PriceDetailCache(1, redissonUtils, key);
    }

    public long changeVersion(Long key) {
        return get(key).changeVersion();
    }


}
