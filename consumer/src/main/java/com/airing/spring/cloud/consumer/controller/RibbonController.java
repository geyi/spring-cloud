package com.airing.spring.cloud.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Ribbon demo
 *
 * @author GEYI
 * @date 2021年04月06日 10:16
 */
@RestController
@RequestMapping("ribbon")
public class RibbonController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("default")
    public Object defaultModel() {
        // choose方法会自动筛选掉状态为DOWN的服务，默认使用轮询的负载均衡策略
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("provider");
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        String url = "http://" + host + ":" + port + "/test/hello";
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        return result;
    }

    @RequestMapping("server/name")
    public Object serverNameModel() {
        String url = "http://provider/test/hello";
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }

}
