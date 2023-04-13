package com.airing.spring.cloud.base.cache.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceCacheValue {
    private String type;
    private String strategy;
}
