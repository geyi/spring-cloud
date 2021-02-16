package com.airing.spring.cloud.consumer.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

	/**
	 * 配置@LoadBalance实现restTemplate可以通过服务名请求服务，如：
	 * http://provider/test/hello
	 *
	 * @return
	 */
	@Bean
	@LoadBalanced
	RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}


	/**
	 * 通过代码配置负载均衡算法
	 *
	 * @return
	 */
    @Bean
	public IRule myRule(){
    	return new RoundRobinRule();
//		return new RandomRule();
	}

}
