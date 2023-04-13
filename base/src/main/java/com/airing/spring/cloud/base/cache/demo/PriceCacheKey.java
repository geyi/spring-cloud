package com.airing.spring.cloud.base.cache.demo;

import com.airing.spring.cloud.base.cache.RedisKey;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
public class PriceCacheKey extends RedisKey {

    private Long priceMetaId;
    private Integer xzqRelationId;
    private String bizType;
    private Integer version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PriceCacheKey that = (PriceCacheKey) o;

        return new EqualsBuilder()
                .append(priceMetaId, that.priceMetaId)
                .append(xzqRelationId, that.xzqRelationId)
                .append(bizType, that.bizType)
                .append(version, that.version)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(priceMetaId)
                .append(xzqRelationId)
                .append(bizType)
                .append(version)
                .toHashCode();
    }

    @Override
    public String uniqueKey() {
        String uniqueKey = super.getUniqueKey();
        if (uniqueKey == null || uniqueKey.length() == 0) {
            Integer cacheVersion = super.getCacheVersion();

            StringBuilder builder = new StringBuilder();
            builder.append("PRICE_")
                    .append(priceMetaId)
                    .append("_")
                    .append(xzqRelationId)
                    .append("_")
                    .append(bizType);
            if (this.version != null) {
                builder.append("_").append(version);
            }
            if (cacheVersion != null) {
                builder.append("_").append(cacheVersion);
            }
            super.setUniqueKey(builder.toString());
        }
        return super.getUniqueKey();
    }
}
