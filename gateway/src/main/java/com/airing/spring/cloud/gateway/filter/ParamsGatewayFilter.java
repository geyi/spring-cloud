package com.airing.spring.cloud.gateway.filter;

import com.airing.spring.cloud.gateway.utils.RequestUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring-cloud-gateway 请求体改造
 *
 * @author GEYI
 * @date 2021年04月06日 10:34
 */
@Component
public class ParamsGatewayFilter implements GatewayFilter {

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*// TODO token
        String token = exchange.getRequest().getHeaders().getFirst(CommonConstant.TOKEN);
        // TODO openId
        String openId = exchange.getRequest().getQueryParams().getFirst("");
        // TODO 获取平台
        String platform = "";
        log.debug("requestId: {}, platform: {}, md5Check: {}",
                exchange.getRequest().getId(),
                platform,
                md5Check);*/

        List<String> fieldList = new ArrayList<>();

        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = request.getHeaders().getContentType();

        // POST 请求 表单请求和JSON参数提交
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

        return refactorPostRequest(serverRequest, mediaType, fieldList, exchange, chain);
    }


    private Mono<Void> refactorPostRequest(ServerRequest serverRequest, MediaType mediaType, List<String> fieldList,
                                           ServerWebExchange exchange, GatewayFilterChain chain) {
        Map<String, Object> params = new HashMap<>();
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .switchIfEmpty(Mono.just("{}"))
                .flatMap(originalBody -> {

                    if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
                        // 表单请求
                        return Mono.just(decodeFormRequest(originalBody, fieldList));
                    }
                    return Mono.just(originalBody.trim());
                });

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders httpHeaders = RequestUtils.httpHeadersBuild(exchange);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, httpHeaders);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    long contentLength = httpHeaders.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0L) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return outputMessage.getBody();
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build());
        }));
    }

    private String decodeFormRequest(String str, List<String> keysList) {
        if ("{}".equals(str)) {
            return null;
        }
        Map<String, Object> map = RequestUtils.paramsToMap(str);
        // TODO 修改参数
        map.put("geyi", "geyi");

        StringBuilder builderStr = new StringBuilder();
        map.forEach((k, v) -> {
            if (builderStr.length() > 0) {
                builderStr.append("&");
            }
            Object val = v;

            builderStr.append(k).append("=").append(val);
        });
        return builderStr.toString();
    }
}
