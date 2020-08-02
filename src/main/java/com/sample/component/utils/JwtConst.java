package com.sample.component.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtConst {
    public static final Integer DEFAULT_EXPIRED = 0;
    public static final Integer ACCESS_EXPIRED = 1;
    public static final Integer REDIS_EXPIRED = 5;
    public static final Integer REFRESH_EXPIRED = 5;

    public static final String SPLIT_TOKEN_SEPARATOR = "\\.";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String PREFIX_KEY = "USER:";

    public static Date getDate(int time) {
        return Date.from(LocalDateTime.now().plusMinutes(time).atZone(ZoneId.systemDefault()).toInstant());
    }
}
