package com.airing.spring.cloud.consumer.facade;

import com.airing.spring.cloud.consumer.service.hystrix.UserConsumerApiFallback;
import com.airing.spring.cloud.consumer.service.hystrix.UserConsumerApiFallbackFactory;
import com.airing.spring.cloud.sdk.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

// Feign整合Hystrix
// @FeignClient(name = "provider", fallback = UserConsumerApiFallback.class)
@FeignClient(name = "provider", fallbackFactory = UserConsumerApiFallbackFactory.class)
public interface UserConsumerApi extends UserApi {
}
