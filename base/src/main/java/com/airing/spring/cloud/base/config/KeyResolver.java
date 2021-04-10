package com.airing.spring.cloud.base.config;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流的键的解析器接口
 * 用户可以自定义实现此接口的类以此来满足不同的限流需求
 *
 * @author GEYI
 * @date 2021年04月10日 12:31
 */
public interface KeyResolver {
    String resolve(HttpServletRequest request);
}
