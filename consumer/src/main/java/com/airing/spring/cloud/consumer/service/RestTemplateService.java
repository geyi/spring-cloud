package com.airing.spring.cloud.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RestTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    AtomicInteger count = new AtomicInteger(1);

    /*
    execution.isolation.strategy：隔离策略，可以是 THREAD（线程池），SEMAPHORE（信号量）。
    execution.isolation.thread.timeoutInMilliseconds：命令执行超时时间，默认值是 1 秒。
    execution.timeout.enabled：是否启用执行超时时间，默认值是 true。
    execution.timeout.enabledForFallback：在降级情况下是否启用执行超时时间，默认值是 true。
    fallback.enabled：是否启用降级，默认值是 true。
    circuitBreaker.enabled：是否启用断路器，默认值是 true。
    circuitBreaker.requestVolumeThreshold：请求阈值，即在滚动窗口中，在此之前必须发生足够多的请求才能计算断路器状态，默认值是 20。
    circuitBreaker.sleepWindowInMilliseconds：断路器打开后的休眠时间，默认值是 5 秒。
    circuitBreaker.errorThresholdPercentage：错误百分比阈值，达到该阈值会触发断路器打开，默认值是 50%。
    metrics.rollingStats.timeInMilliseconds：滚动窗口的持续时间，默认值是 10000 毫秒。
    metrics.rollingStats.numBuckets：滚动窗口中的桶数，默认值是 10。
     */
    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
//            @HystrixProperty(name = "fallback.enabled", value = "true"),
//            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "40"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "30000")
    })
    public String alive() {
        /*if (count.getAndIncrement() <= 3) {
            System.out.println(1 / 0);
        }*/

        if (count.getAndIncrement() % 2 == 0) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1 / 0);
        }
        return "s";
        /*String url ="http://provider/alive";
        String object = restTemplate.getForObject(url, String.class);
        return object;*/
    }

    public String fallback() {
        return "请求失败";
    }

}
