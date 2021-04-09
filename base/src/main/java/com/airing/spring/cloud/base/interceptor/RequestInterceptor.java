package com.airing.spring.cloud.base.interceptor;

import com.airing.spring.cloud.base.Constant;
import com.airing.spring.cloud.base.annotation.AccessLimit;
import com.airing.spring.cloud.base.annotation.Auth;
import com.airing.spring.cloud.base.annotation.Sign;
import com.airing.spring.cloud.base.config.KeyResolver;
import com.airing.spring.cloud.base.entity.RequestContext;
import com.airing.spring.cloud.base.enums.ExceptionEnum;
import com.airing.spring.cloud.base.exception.BusinessException;
import com.airing.spring.cloud.base.filter.RequestWrapper;
import com.airing.spring.cloud.base.utils.IPUtils;
import com.airing.spring.cloud.base.utils.MD5Utils;
import com.airing.spring.cloud.base.utils.RedissonUtils;
import com.airing.spring.cloud.base.utils.RequestUtils;
import com.airing.spring.cloud.base.utils.SpringBeanUtils;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    protected static final String REQ_RATE_LIMITER_TOKENS = "REQ_RATE_LIMITER_{%s}_TOKENS";
    protected static final String REQ_RATE_LIMITER_TIMESTAMP = "REQ_RATE_LIMITER_{%s}_TIMESTAMP";

    @Autowired(required = false)
    private RedissonUtils redissonUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

        RequestContext context = RequestContext.getContext();
        context.setLang(RequestUtils.getHeader(request, Constant.LANGUAGE_HEAD, Constant.DEFAULT_LANG));
        context.setAppId(request.getHeader(Constant.APP_ID_HEAD));
        context.setIp(IPUtils.getIp(request));
        context.setStartTime(System.currentTimeMillis());

        // 根据token获取用户id
        String token = request.getHeader(Constant.TOKEN_HEAD);
        if (token != null && token.length() > 0 && redissonUtils != null) {
            String userId = this.redissonUtils.get(token, StringCodec.INSTANCE);
            if (userId != null && userId.length() > 0) {
                context.setUserId(Long.valueOf(userId));
            }
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            boolean accessLimit = this.accessLimit(handlerMethod, request);
            if (!accessLimit) {
                throw new BusinessException(ExceptionEnum.LIMIT_ERROR);
            }

            boolean sign = this.sign(handlerMethod, request);
            if (!sign) {
                throw new BusinessException(ExceptionEnum.SIGN_ERROR);
            }

            boolean auth = this.auth(handlerMethod, request);
            if (!auth) {
                throw new BusinessException(ExceptionEnum.AUTH_ERROR);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
//            log.info("{} RT:{}ms", method.getMethod().getName(),
//                    (System.currentTimeMillis() - RequestContext.getContext().getStartTime()));
            RequestContext.removeContext();
        }
    }

    /**
     * 参数签名校验
     *
     * @param handlerMethod
     * @param request
     * @return boolean
     * @author GEYI
     * @date 2021年04月01日 10:47
     */
    private boolean sign(HandlerMethod handlerMethod, HttpServletRequest request) {
        // 是否需要进行参数签名校验
        Sign signAnnotation = handlerMethod.getMethodAnnotation(Sign.class);
        if (signAnnotation != null) {
            String method = request.getMethod();
            TreeMap<String, Object> signMap = new TreeMap<>();
            if (Constant.GET_METHOD.equals(method)) {
                // put需要参与签名的参数
                String queryString = request.getQueryString();
                if (queryString != null && queryString.length() > 0) {
                    RequestUtils.paramsToMap(queryString, signMap);
                }
            } else if (Constant.POST_METHOD.equals(method)) {
                if (request instanceof RequestWrapper) {
                    RequestWrapper requestWrapper = (RequestWrapper) request;
                    String bodyStr = requestWrapper.getBodyString();
                    // raw（参数放在请求体内）
//                    log.debug("请求体参数|{}", bodyStr);
                    // put参与签名的请求体
                    signMap.put("body", bodyStr);

                    /*Map<String, Object> map = new HashMap<>();
                    map.put("a", "b");
                    requestWrapper.setBody(JSONObject.toJSONString(map));*/
                }
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName.startsWith(Constant.SIGN_HEAD_PREFIX)) {
                    String headerValue = request.getHeader(headerName);
                    if (headerValue == null || headerValue.length() == 0) {
                        continue;
                    }
                    // put需要参与签名的请求头
                    signMap.put(headerName.substring(Constant.SIGN_HEAD_PREFIX.length()), headerValue);
                }
            }
            if (!signMap.isEmpty()) {
                String md5 = MD5Utils.getMD5String(signMap, Constant.SIGN_KEY);
                String sign = request.getHeader(Constant.SIGNATURE_HEAD);
                if (sign == null || !sign.equals(md5.toUpperCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * token有效性校验
     *
     * @param handlerMethod
     * @param request
     * @return boolean
     * @author GEYI
     * @date 2021年04月02日 9:09
     */
    private boolean auth(HandlerMethod handlerMethod, HttpServletRequest request) {
        Auth authAnnotation = handlerMethod.getMethodAnnotation(Auth.class);
        if (authAnnotation != null) {
            Long userId = RequestContext.getContext().getUserId();
            if (userId == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 限流器
     *
     * @param handlerMethod
     * @param request
     * @return boolean
     * @author GEYI
     * @date 2021年04月09日 15:22
     */
    private boolean accessLimit(HandlerMethod handlerMethod, HttpServletRequest request) {
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
        if (accessLimit != null) {
            int rate = accessLimit.replenishRate();
            int capacity = accessLimit.burstCapacity();
            int now = (int) (System.currentTimeMillis() / 1000);
            int requested = 1;
            KeyResolver keyResolver = SpringBeanUtils.getBean(accessLimit.keyResolver());
            String key = keyResolver.resolve(request);
            String tokensKey = String.format(REQ_RATE_LIMITER_TOKENS, key);
            String timestampKey = String.format(REQ_RATE_LIMITER_TIMESTAMP, key);
            String lastTokensStr = this.redissonUtils.get(tokensKey);
            int lastTokens;
            if (lastTokensStr == null || lastTokensStr.length() == 0) {
                lastTokens = capacity;
            } else {
                lastTokens = Integer.parseInt(lastTokensStr);
            }
            String lastRefreshedStr = this.redissonUtils.get(timestampKey);
            int lastRefreshed;
            if (lastRefreshedStr == null || lastRefreshedStr.length() == 0) {
                lastRefreshed = 0;
            } else {
                lastRefreshed = Integer.parseInt(lastRefreshedStr);
            }
//            log.debug("accessLimit|{}|{}|{}|{}|{}", rate, capacity, now, lastTokens, lastRefreshed);
            int delta = Math.max(0, now - lastRefreshed);
            int filledTokens = Math.min(capacity, lastTokens + (delta * rate));
            boolean allowed = filledTokens >= requested;
            int newTokens = filledTokens;
            if (allowed) {
                newTokens = filledTokens - requested;
            } else {
//                log.warn("可疑ip|{}", key);
            }

            int fillTime = capacity / rate;
            int ttl = fillTime * 2;
            if (ttl > 0) {
                this.redissonUtils.set(tokensKey, String.valueOf(newTokens), ttl, TimeUnit.SECONDS);
                this.redissonUtils.set(timestampKey, String.valueOf(now), ttl, TimeUnit.SECONDS);
            }
            return allowed;
        }
        return true;
    }

}
