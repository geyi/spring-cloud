package com.airing.spring.cloud.gateway.filter;

import com.airing.spring.cloud.gateway.utils.RequestUtils;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ParamsGatewayFilter implements GatewayFilter {

    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        ServerRequest serverRequest = ServerRequest.create(exchange, MESSAGE_READERS);

        return refactorPostRequest(serverRequest, exchange, chain);
    }


    private Mono<Void> refactorPostRequest(ServerRequest serverRequest, ServerWebExchange exchange,
                                           GatewayFilterChain chain) {

        return serverRequest.bodyToMono(String.class)
                .switchIfEmpty(Mono.just("{}"))
                .flatMap(originalBody -> {
                    Map<String, Object> params = RequestUtils.paramsToMap(originalBody);
                    System.out.println("params: " + params);
                    if (false) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    } else {
                        BodyInserter bodyInserter = BodyInserters.fromPublisher(Mono.just(originalBody), String.class);
                        HttpHeaders httpHeaders = RequestUtils.httpHeadersBuild(exchange);
                        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, httpHeaders);
                        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                            ServerHttpRequestDecorator decorator =
                                    new ServerHttpRequestDecorator(exchange.getRequest()) {
                                        @Override
                                        public Flux<DataBuffer> getBody() {
                                            return outputMessage.getBody();
                                        }
                                    };
                            return chain.filter(exchange.mutate().request(decorator).build());
                        }));
                    }
                });
    }
}
