package com.sample.config;

import com.sample.domain.dtos.AccountBasicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    /* application-redis.yml 내의 property 파일 설정을 읽어오는 클래스 */
    @Autowired
    RedisProperties redisProperties;

    /* Redis Connector */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        /* valueOperations 사용하는 경우 redis-cli 에서 keys * 조회시 키 값들의 앞에 \xac\xed\x00\x05t\x00\x04 붙는거 제거 */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        /* valueOperations 사용하는 경우 redis-cli 에서 get 'key' 조회 시 value 값에 \xac\xed\x00\x05t\x00\x04 붙는거 제거 */
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        /* hashOperations<String, Object, Object>를 사용하는 경우 두 번째 파라미터가 HashKey */
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        /* hashOperations<String, Object, Object>를 사용하는 경우 세 번째 파라미터가 HashValue */
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(AccountBasicInfo.class));

        /* 트랜잭션 ?*/
        redisTemplate.setEnableTransactionSupport(true);

        /* redisTemplate은 redisConnectionFactory을 기반으로 돌아감 */
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

        /* StringRedisTempalte의 값을 User 객체로 사용하는 경우 jsontype으로 serialize 해야 하기 때문에 설정 */
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());

        /* redis의 트랜잭션 ? */
        stringRedisTemplate.setEnableTransactionSupport(true);
        /* stringRedisTemplate은 redisConnectionFactory을 기반으로 돌아감 */
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}
