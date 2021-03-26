package com.airing.spring.cloud.provider.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class RequestParseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestContext.getContext().setStartTime(System.currentTimeMillis());
        RequestContext.getContext().setAddress(request.getHeader("address"));
        RequestContext.getContext().setPlatform(request.getHeader("platform"));
        RequestContext.getContext().setAppId(request.getHeader("appid"));
        RequestContext.getContext().setVersionCode(request.getHeader("versionCode"));
        RequestContext.getContext().setSource(request.getHeader("source"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
//            log.info("{} RT:{}ms", method.getMethod().getName(),
//                    (System.currentTimeMillis() - RequestContext.getContext().getStartTime()));
            System.out.println("RT:" + (System.currentTimeMillis() - RequestContext.getContext().getStartTime()));
        }
        RequestContext.removeContext();
    }

}
