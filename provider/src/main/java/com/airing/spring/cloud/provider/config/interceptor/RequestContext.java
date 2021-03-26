package com.airing.spring.cloud.provider.config.interceptor;

public class RequestContext {

    private RequestContext() {}

    private String token;
    private Long userId;
    private String ip;
    private Long startTime;
    private String address;
    private String platform;
    private String appId;
    private String versionCode;
    private String source;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private final static ThreadLocal<RequestContext> LOCAL = ThreadLocal.withInitial(() -> new RequestContext());

    public static final RequestContext getContext() {
        return LOCAL.get();
    }

    public static final void removeContext() {
        LOCAL.remove();
    }

}
