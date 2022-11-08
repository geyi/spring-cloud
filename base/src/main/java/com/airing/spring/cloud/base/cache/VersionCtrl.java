package com.airing.spring.cloud.base.cache;

public interface VersionCtrl<K, V> {
    V get(K key);
}
