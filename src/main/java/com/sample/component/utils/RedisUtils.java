package com.sample.component.utils;

import com.sample.domain.dtos.AccountBasicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.sample.component.utils.JwtConst.getDate;

/**
 * Redis 핸들링을 위한 유틸
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, AccountBasicInfo> hashOperations;

    public Boolean hasKey(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    public AccountBasicInfo get(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    public void makeRefreshTokenAndExpiredAt(String signature, String accessToken, AccountBasicInfo accountBasicInfo) {
        hashOperations.put(JwtConst.PREFIX_KEY + signature, accessToken, accountBasicInfo);
        redisTemplate.expireAt(JwtConst.PREFIX_KEY + signature, getDate(JwtConst.REDIS_EXPIRED));
    }
}
