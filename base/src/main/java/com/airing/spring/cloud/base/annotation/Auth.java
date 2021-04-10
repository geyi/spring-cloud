package com.airing.spring.cloud.base.annotation;

import com.airing.spring.cloud.base.Constant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口鉴权
 *
 * @author GEYI
 * @date 2021年04月10日 18:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    // token在请求头中的headerName
    String tokenName() default Constant.TOKEN_HEAD;
}
