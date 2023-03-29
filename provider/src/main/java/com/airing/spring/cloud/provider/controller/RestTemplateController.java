package com.airing.spring.cloud.provider.controller;

import com.airing.spring.cloud.facade.entity.User;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest")
public class RestTemplateController {

    @RequestMapping("get")
    public Object get(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("AirIng");
        return user;
    }

    @RequestMapping("post")
    public Object post(@RequestBody User user) {
        user.setUsername(user.getUsername() + "666");
        return user;
    }

    @RequestMapping("postJSON")
    public Object postJSON(@RequestBody String content) {
        return content;
    }

    @RequestMapping("location")
    public Object location(@RequestBody String keyword, HttpServletResponse response) throws URISyntaxException {
        URI uri = new URI("https://www.baidu.com/s?wd=" + keyword);
        response.addHeader("Location", uri.toString());
        return uri;
    }

}
