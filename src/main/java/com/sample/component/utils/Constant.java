package com.sample.component.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface Constant {

    class JwtConst {
        static final String SUBJECT = "/auth/login";
        static final String ISSUER = "seok";
        static final String AUDIENCE = "client";
        public static String SPLIT_TOKEN_SEPARATOR = "\\.";
        public static String BEARER = "Bearer ";
        public static String ACCESS_TOKEN = "accessToken";
        public static String REFRESH_TOKEN = "refresh_token";

    }
    class RedisConst {
        public static String PREFIX_KEY = "USER:";
        public static String WAS_LOG = "WAS:LOG";
        public static Integer DEFAULT_EXPIRED = 0;
        public static Integer ACCESS_EXPIRED = 2;
        public static Integer REDIS_EXPIRED = 6;
        public static Integer REFRESH_EXPIRED = 6;

    }

    static Date getDate(int time) {
        return Date.from(LocalDateTime.now().plusMinutes(time).atZone(ZoneId.systemDefault()).toInstant());
    }
}
