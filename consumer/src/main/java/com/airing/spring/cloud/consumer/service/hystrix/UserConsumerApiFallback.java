package com.airing.spring.cloud.consumer.service.hystrix;

import com.airing.spring.cloud.consumer.facade.UserConsumerApi;
import com.airing.spring.cloud.sdk.entity.User;
import org.springframework.stereotype.Component;

// @Component
public class UserConsumerApiFallback implements UserConsumerApi {
    @Override
    public User getUserInfo(Long id) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public String alive() {
        return "降级了";
    }
}
