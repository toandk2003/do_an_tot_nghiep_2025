package org.yenln8.ChatApp.common.util;


import jakarta.servlet.http.HttpServletRequest;

public class Network {
    public static String getUserIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
