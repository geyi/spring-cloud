package com.airing.spring.cloud.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController {

    @Value("${server.port}")
    private String port;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("hello")
    public Object hello() {
        return "HELLO" + port;
    }

    @PostMapping("test")
    public Object test(HttpServletRequest request) {
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue()[0]);
        }
        return port;
    }

}
