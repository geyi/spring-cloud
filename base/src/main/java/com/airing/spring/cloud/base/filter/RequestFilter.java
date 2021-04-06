package com.airing.spring.cloud.base.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 在启动类上增加注释@ServletComponentScan(basePackages = "com.agogopost.intl.base.filter")来激活此拦截器
 *
 * @author GEYI
 * @date 2021年03月30日 10:23
 */
@WebFilter(filterName = "requestFilter", urlPatterns = "/*")
public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && MediaType.APPLICATION_JSON_VALUE.contains(contentType)) {
            filterChain.doFilter(new RequestWrapper(request), servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
