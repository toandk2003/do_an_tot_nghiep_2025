package org.yenln8.ChatApp.common.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class AuthConstant {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";
    public static final long ACCESS_TOKEN_EXPIRATION = 100 * 24 * 60 * 60 * 1000L;
    public static final long MAX_DEVICE = 2;
    public static final long LOGIN_TRY_AGAIN_TIME_IN_MINUTES = 1;
    public static final long LOGIN_FAIL_LIMIT = 5;
}