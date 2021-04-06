package com.airing.spring.cloud.consumer.controller;

import com.airing.spring.cloud.base.annotation.Auth;
import com.airing.spring.cloud.base.annotation.Sign;
import com.airing.spring.cloud.base.enums.ExceptionEnum;
import com.airing.spring.cloud.base.exception.BusinessException;
import com.netflix.discovery.EurekaClient;
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

@RestController
@RequestMapping("test")
public class TestController {

    // Spring定义的接口
    @Autowired
    private DiscoveryClient discoveryClient;
    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

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
    @Sign
    private Object test(/*@RequestBody String body,*/
                        @RequestParam(required = false) String number) {
        System.out.println(number);
        return "";
    }



    @GetMapping("/i18n")
    public Object i18n(String key) {
        try {
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw new BusinessException(ExceptionEnum.JUJU);
        }
        return "";
    }

}
