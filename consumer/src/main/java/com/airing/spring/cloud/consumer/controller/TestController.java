package com.airing.spring.cloud.consumer.controller;

import com.airing.spring.cloud.base.annotation.AccessLimit;
import com.airing.spring.cloud.base.annotation.Auth;
import com.airing.spring.cloud.base.annotation.Sign;
import com.airing.spring.cloud.base.enums.ExceptionEnum;
import com.airing.spring.cloud.base.exception.BusinessException;
import com.airing.spring.cloud.base.utils.RedissonUtils;
import com.airing.spring.cloud.base.utils.http.HttpRequestUtils;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    // Spring定义的接口
    @Autowired
    private DiscoveryClient discoveryClient;
    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RedissonUtils redissonUtils;

    @GetMapping("get/services")
    public Object getServices() {
        return this.discoveryClient.getServices();
    }

    @GetMapping("get/instances")
    public Object getInstances(String type, boolean vip, String serviceId) {
        if ("eureka".equals(type)) {
            if (vip) {
                // serviceId对应<application> -> <name>标签的值，不区分大小写
                return this.eurekaClient.getInstancesByVipAddress(serviceId, false);
            } else {
                // serviceId对应<application> -> <instance> -> <instanceId>标签的值
                return this.eurekaClient.getInstancesById(serviceId);
            }
        } else {
            // http://localhost:9001/eureka/apps
            // serviceId对应<application> -> <name>标签的值，不区分大小写
            return this.discoveryClient.getInstances(serviceId);
        }
    }

    @GetMapping("invoke")
    private Object invoke(String serviceId) {
        ServiceInstance serviceInstance = this.loadBalancerClient.choose(serviceId);
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        String url = "http://" + host + ":" + port + "/test/hello";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        return result;
    }

    @RequestMapping("test")
//    @Sign
    @AccessLimit
    private Object test(/*@RequestBody String body,*/
                        @RequestParam(required = false) String number) {
//        System.out.println(number);
        log.info(redissonUtils.get(number, StringCodec.INSTANCE));
        return "success";
    }

    @GetMapping("/i18n")
    public Object i18n(String key) {
        /*try {
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw new BusinessException(ExceptionEnum.JUJU);
        }*/
        RateLimiter rateLimiter = RateLimiter.create(10.0);
        for (int i = 0; i < 30; i++) {
            rateLimiter.acquire();
            new Thread(() -> {
                String respStr = HttpRequestUtils.get("http://localhost:9200/test/test");
                System.out.println((System.currentTimeMillis() / 1000) + respStr);
            }).start();
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        RateLimiter rateLimiter = RateLimiter.create(3.0);
        while (true) {
            rateLimiter.acquire();
            new Thread(() -> {
                String respStr = HttpRequestUtils.get("http://localhost:9200/test/test");
                System.out.println(respStr);
            }).start();
        }
    }

}
