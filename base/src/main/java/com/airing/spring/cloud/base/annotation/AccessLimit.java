package com.airing.spring.cloud.base.annotation;

import com.airing.spring.cloud.base.config.DefaultKeyResolver;
import com.airing.spring.cloud.base.config.KeyResolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流器
 * 参考spring-cloud-gateway-server.jar中scripts/request_rate_limiter.lua的实现
 *
 * @author GEYI
 * @date 2021年04月10日 10:17
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {
    // 每秒补充令牌的速率
    int replenishRate() default 1;

    // 令牌桶容量
    int burstCapacity() default 3;

    // 用于限流的键的解析器
    Class<? extends KeyResolver> keyResolver() default DefaultKeyResolver.class;

}
