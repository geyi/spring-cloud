package com.airing.spring.cloud.base.cache.demo;

import com.airing.spring.cloud.base.cache.RedisKey;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
public class DefaultPriceCacheKey extends RedisKey {
    private String channel;
    private String expressCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DefaultPriceCacheKey that = (DefaultPriceCacheKey) o;

        return new EqualsBuilder()
                .append(channel, that.channel)
                .append(expressCode, that.expressCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(channel)
                .append(expressCode)
                .toHashCode();
    }

    @Override
    public String uniqueKey() {
        String uniqueKey = super.getUniqueKey();
        Integer cacheVersion = super.getCacheVersion();
        if (uniqueKey == null || uniqueKey.length() == 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("PRICE_")
                    .append(channel)
                    .append("_")
                    .append(expressCode);
            if (cacheVersion != null) {
                builder.append("_").append(cacheVersion);
            }
            super.setUniqueKey(builder.toString());
        }
        return super.getUniqueKey();
    }
}
