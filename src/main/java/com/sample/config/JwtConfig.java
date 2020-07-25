package com.sample.config;

import com.sample.component.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(secret);
    }
}
