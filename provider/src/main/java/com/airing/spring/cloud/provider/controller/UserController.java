package com.airing.spring.cloud.provider.controller;

import com.airing.spring.cloud.facade.api.UserApi;
import com.airing.spring.cloud.facade.entity.User;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    private AtomicInteger count = new AtomicInteger(0);

    @Value("${server.port}")
    private String port;

    @Override
    public User getUserInfo(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("juju");
        return user;
    }

    @Override
    public User saveUser(User user) {
        user.setUsername(user.getUsername() + "666");
        return user;
    }

    @Override
    public String alive() {
        /*int i = count.incrementAndGet();
        System.out.println("第" + i + "次调用");
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        System.out.println(1 / 0);

        return port;
    }
}
