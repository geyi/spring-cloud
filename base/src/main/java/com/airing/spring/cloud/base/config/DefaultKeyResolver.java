package com.airing.spring.cloud.base.config;

import com.agogopost.intl.base.utils.IPUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DefaultKeyResolver implements KeyResolver {
    @Override
    public String resolve(HttpServletRequest request) {
        String ip = IPUtils.getIp(request);
        return ip;
    }
}
