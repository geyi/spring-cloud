package com.airing.spring.cloud.base.utils.bloom;

import com.airing.spring.cloud.base.Constant;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;

/**
 * 黑名单布隆过滤器
 *
 * @author GEYI
 * @date 2021年04月29日 18:14
 */
public class ExpressQueryBLBloomFilterUtils {
    private static volatile ExpressQueryBLBloomFilterUtils single = null;
    private static final Object lock = new Object();
    private static volatile RedisBloomFilter<String> filter = null;
    private static final long EXPECTED_INSERTIONS = 100000;
    private static final double FPP = 0.001;

    private ExpressQueryBLBloomFilterUtils() {}

    public static ExpressQueryBLBloomFilterUtils getSingle() {
        if (single == null) {
            synchronized (lock) {
                if (single == null) {
                    single = new ExpressQueryBLBloomFilterUtils();
                    filter = RedisBloomFilter.create(Funnels.stringFunnel(Charset.forName(Constant.DEFAULT_CHARSET)),
                            EXPECTED_INSERTIONS, FPP, Constant.DEFAULT_BLOOM_FILTER_NAME);
                }
            }
        }
        return single;
    }

    public boolean put(String ip) {
        return filter.put(ip);
    }

    public boolean mightContain(String ip) {
        return filter.mightContain(ip);
    }
}
