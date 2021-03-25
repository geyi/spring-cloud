package com.airing.spring.cloud.provider.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("test")
    public Object test(HttpServletRequest request) {
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue()[0]);
        }
        return port;
    }

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/i18n")
    public Object i18n(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

}
