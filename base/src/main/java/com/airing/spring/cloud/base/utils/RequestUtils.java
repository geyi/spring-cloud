package com.airing.spring.cloud.base.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUtils {
    /**
     * 参数形如：k1=v2&k2=v2
     *
     * @param params
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author GEYI
     * @date 2021年04月01日 10:34
     */
    public static Map<String, Object> paramsToMap(String params) {
        return Arrays.stream(params.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length > 1)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    /**
     * 参数形如：k1=v2&k2=v2
     *
     * @param str
     * @param result
     * @author GEYI
     * @date 2021年04月01日 10:34
     */
    public static void paramsToMap(String str, Map<String, Object> result) {
        result.putAll(Arrays.stream(str.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length > 1)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])));
    }

    /**
     * 获取请求头，提供一个默认值
     *
     * @param request
     * @param headerName
     * @param defaultValue
     * @return java.lang.String
     * @author GEYI
     * @date 2021年04月01日 18:59
     */
    public static String getHeader(HttpServletRequest request, String headerName, String defaultValue) {
        String headerValue = request.getHeader(headerName);
        if (headerValue == null || headerValue.length() == 0) {
            return defaultValue;
        }
        return headerValue;
    }
}
