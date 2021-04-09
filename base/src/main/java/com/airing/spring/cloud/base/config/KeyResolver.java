package com.airing.spring.cloud.base.config;

import javax.servlet.http.HttpServletRequest;

public interface KeyResolver {
    String resolve(HttpServletRequest request);
}
