package com.airing.spring.cloud.base.config;

import com.airing.spring.cloud.base.utils.IPUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认的限流的键的解析器
 * 默认根据IP地址限流
 *
 * @author GEYI
 * @date 2021年04月10日 12:30
 */
@Component
public class DefaultKeyResolver implements KeyResolver {
    @Override
    public String resolve(HttpServletRequest request) {
        String ip = IPUtils.getIp(request);
        return ip;
    }
}
