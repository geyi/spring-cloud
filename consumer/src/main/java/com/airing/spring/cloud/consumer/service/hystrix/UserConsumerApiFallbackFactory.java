package com.airing.spring.cloud.consumer.service.hystrix;

import com.airing.spring.cloud.consumer.facade.UserConsumerApi;
import com.airing.spring.cloud.facade.entity.User;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 可以根据不同的异常定制不同的响应
 */
@Component
public class UserConsumerApiFallbackFactory implements FallbackFactory<UserConsumerApi> {
    @Override
    public UserConsumerApi create(Throwable throwable) {
        return new UserConsumerApi() {
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
                throwable.printStackTrace();
                if (throwable instanceof FeignException.InternalServerError) {
                    return "服务器返回500";
                } else {
                    return "其它异常";
                }
            }
        };
    }
}
