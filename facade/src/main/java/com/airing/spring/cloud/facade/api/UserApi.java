package com.airing.spring.cloud.facade.api;

import com.airing.spring.cloud.facade.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 要使用hystrix就不能在接口上添加@RequestMapping注解，否则consumer将无法启动
 */
public interface UserApi {

    /**
     * get请求的参数传递需要添加@RequestParam
     * @param id
     * @return
     */
    @GetMapping("user/info")
    User getUserInfo(@RequestParam("id") Long id);

    /**
     * post请求的参数传递需要添加@RequestBody
     * @param user
     * @return
     */
    @PostMapping("user/save")
    User saveUser(@RequestBody User user);

    @GetMapping("user/alive")
    String alive();

}
