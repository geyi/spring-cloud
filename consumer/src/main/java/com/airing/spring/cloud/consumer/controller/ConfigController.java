package com.airing.spring.cloud.consumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
// 支持手动刷新配置
@RefreshScope
public class ConfigController {

    // 使用spring cloud config时，配置文件必须命令为bootstrap.yml，否则无法读取到配置
    @Value("${sms.title.template}")
    private String smsTitleTemplate;

    @RequestMapping("")
    public Object config() {
        return smsTitleTemplate;
    }

}
