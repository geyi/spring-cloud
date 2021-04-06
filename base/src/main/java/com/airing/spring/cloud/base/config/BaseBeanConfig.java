package com.airing.spring.cloud.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class BaseBeanConfig {

    /**
     * 注册自定义本地化解析策略bean
     *
     * @return org.springframework.web.servlet.LocaleResolver
     * @author GEYI
     * @date 2021年03月30日 9:25
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }

}
