package com.airing.spring.cloud.base.utils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);
        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
        }

        // String ip = request.getRemoteAddr();

        if (ip != null && ip.length() != 0) {
            if (ip.contains(",")) {
                String[] array = ip.split(",");
                for (String s : array) {
                    if (s != null && !("unknown".equalsIgnoreCase(s))) {
                        ip = s;
                        break;
                    }
                }
            }
        }
        if (ip == null)
            ip = "";
        return ip.trim();
    }
}
