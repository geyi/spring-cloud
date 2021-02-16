package com.airing.spring.cloud.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @Value("${server.port}")
    private String port;

    @GetMapping("hello")
    public Object hello() {
        return "HELLO" + port;
    }

}
