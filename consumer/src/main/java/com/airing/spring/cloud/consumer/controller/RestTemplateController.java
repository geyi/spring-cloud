package com.airing.spring.cloud.consumer.controller;

import com.airing.spring.cloud.consumer.service.RestTemplateService;
import com.airing.spring.cloud.sdk.entity.User;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate demo
 * 通过RestTemplate调用远程接口
 * RestTemplate + @LoadBalance实现通过服务名调用远程接口
 * RestTemplate整合Hystrix
 *
 * @author GEYI
 * @date 2021年04月06日 10:13
 */
@RestController
@RequestMapping("rest")
public class RestTemplateController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateService restTemplateService;

    @RequestMapping("get")
    public Object get(Long id) {
        String url = "http://provider/rest/get?id={1}";
        User user = restTemplate.getForObject(url, User.class, id);
        return user;
    }

    @RequestMapping("postForm")
    public Object postForm(User user) {
        String url = "http://provider/rest/post";
        User newUser = restTemplate.postForObject(url, user, User.class);
        return newUser;
    }

    @RequestMapping("postBody")
    public Object postBody(@RequestBody String content) {
        String url = "http://provider/rest/postJSON";
        String user = restTemplate.postForObject(url, content, String.class);
        return user;
    }

    @RequestMapping("location")
    public Object location(String keyword, HttpServletResponse response) throws IOException {
        String url = "http://provider/rest/location";
        URI uri = restTemplate.postForLocation(url, keyword);
        response.sendRedirect(uri.toString());
        return null;
    }

    /**
     * RestTemplate整合Hystrix
     * @return
     */
    @GetMapping("alive")
    public Object alive() {
        return this.restTemplateService.alive();
    }

}
