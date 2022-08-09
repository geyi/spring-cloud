package com.airing.spring.cloud.gateway.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public class RequestUtils {

    /**
     * 参数形如：k1=v2&k2=v2
     *
     * @param params
     * @return
     */
    public static Map<String, Object> paramsToMap(String params) {
        return Arrays.stream(params.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> (arr.length > 1 ? arr[1] : "")));
    }

    /**
     * 参数形如：k1=v2&k2=v2
     *
     * @param str
     * @param result
     */
    public static void paramsToMap(String str, Map<String, Object> result) {
        result.putAll(Arrays.stream(str.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> (arr.length > 1 ? arr[1] : ""))));
    }

    public static HttpHeaders httpHeadersBuild(ServerWebExchange exchange) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        return headers;
    }

}
