package com.airing.spring.cloud.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口参数签名
 *
 * @author GEYI
 * @date 2021年04月10日 18:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sign {
    // 签名算法，暂时没用到
    String algorithm() default "MD5";
}
