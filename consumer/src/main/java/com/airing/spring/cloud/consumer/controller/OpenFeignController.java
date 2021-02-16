package com.airing.spring.cloud.consumer.controller;

import com.airing.spring.cloud.consumer.facade.UserConsumerApi;
import com.airing.spring.cloud.sdk.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("open/feign")
public class OpenFeignController {

    @Autowired
    private UserConsumerApi userConsumerApi;

    @GetMapping("getUser")
    public Object getUser(Long id) {
        return this.userConsumerApi.getUserInfo(id);
    }

    @PostMapping("saveUser")
    public Object saveUser(User user) {
        return this.userConsumerApi.saveUser(user);
    }

    @GetMapping("alive")
    public Object alive() {
        return this.userConsumerApi.alive();
    }

}
