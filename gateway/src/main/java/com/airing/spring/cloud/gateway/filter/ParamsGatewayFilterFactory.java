package com.airing.spring.cloud.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class ParamsGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private ParamsGatewayFilter paramsGatewayFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return paramsGatewayFilter;
    }
}
