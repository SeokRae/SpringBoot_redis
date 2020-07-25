package com.sample.component.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtConst {
    public static final Integer DEFAULT_EXPIRED = 0;
    public static final Integer ACCESS_EXPIRED = 1;
    public static final Integer REDIS_EXPIRED = 3;
    public static final Integer REFRESH_EXPIRED = 5;

    public static Date getDate(int time) {
        return Date.from(LocalDateTime.now().plusMinutes(time).atZone(ZoneId.systemDefault()).toInstant());
    }
}
