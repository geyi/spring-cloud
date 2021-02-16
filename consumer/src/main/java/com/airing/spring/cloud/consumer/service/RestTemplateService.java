package com.airing.spring.cloud.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallback")
    public String alive() {
        String url ="http://provider/alive";
        String object = restTemplate.getForObject(url, String.class);
        return object;
    }

    public String fallback() {
        return "请求失败";
    }

}
