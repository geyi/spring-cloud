package com.airing.spring.cloud.base.cache;

import lombok.Getter;

@Getter
public abstract class RedisKey {
    private Integer cacheVersion;
    private String uniqueKey;

    public void setCacheVersion(Integer cacheVersion) {
        this.cacheVersion = cacheVersion;
        this.uniqueKey = null;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getKey(String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(uniqueKey()).append(suffix);
        return sb.toString();
    }

    public String getKeyWithSuffix(String suffix) {
        return getKey("", suffix);
    }

    public String getKeyWithPrefix(String prefix) {
        return getKey(prefix, "");
    }

    public abstract String uniqueKey();

}
