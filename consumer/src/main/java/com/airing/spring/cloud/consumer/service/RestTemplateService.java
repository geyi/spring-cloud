package com.airing.spring.cloud.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RestTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    AtomicInteger count = new AtomicInteger(1);

    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
//            @HystrixProperty(name = "fallback.enabled", value = "true"),
//            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "40"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "30000")
    })
    public String alive() {
        /*if (count.getAndIncrement() <= 3) {
            System.out.println(1 / 0);
        }*/

        if (count.getAndIncrement() % 2 == 0) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1 / 0);
        }
        return "s";
        /*String url ="http://provider/alive";
        String object = restTemplate.getForObject(url, String.class);
        return object;*/
    }

    public String fallback() {
        return "请求失败";
    }

}
