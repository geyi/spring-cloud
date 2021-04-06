package com.airing.spring.cloud.base;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BaseMain {
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown-ip";
        }
    }
}
