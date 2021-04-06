package com.airing.spring.cloud.base.entity;

/**
 * 请求上下文
 *
 * @author GEYI
 * @date 2021年03月31日 9:28
 */
public class RequestContext {

    private RequestContext() {}

    private String token;
    private Long userId;
    private String tra;
    private String platform;
    private String ip;
    private Long startTime;
    private String lang;
    private String appId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTra() {
        return tra;
    }

    public void setTra(String tra) {
        this.tra = tra;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    private final static ThreadLocal<RequestContext> LOCAL = ThreadLocal.withInitial(() -> new RequestContext());

    public static final RequestContext getContext() {
        return LOCAL.get();
    }

    public static final void removeContext() {
        LOCAL.remove();
    }

}
