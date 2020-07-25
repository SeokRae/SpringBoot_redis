package com.sample.config;

import com.sample.component.interceptor.AuthenticationInterceptor;
import com.sample.component.interceptor.AuthorizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /* 인가 / 권한 관리 인터셉터 */
    @Bean
    public HandlerInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    /* 인증 관리 인터셉터 */
    @Bean
    public HandlerInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /* 인증 관리 인터셉터 설정 */
        registry
                .addInterceptor(authenticationInterceptor())
                .addPathPatterns(
                        "/auth/login/**"        /* 로그인 시 인증 처리                          */
                )
                /* 인증 처리 제외 경로 설정 */
                .excludePathPatterns(
                        "/account/add/**"       /* 사용자 등록 */
                        , "/account/all"        /* 사용자 목록 조회 */
                );

        /* 인가 / 권한 인터셉터 설정 */
        registry
                .addInterceptor(authorizationInterceptor())
                /* 인가 / 권한이 필요한 경로 설정 */
                .addPathPatterns(
                        "/auth/user"
                        , "/auth/admin"
                        , "/account/update/**"

                )
                /* 인가 / 권한 제외 경로 설정 */
                .excludePathPatterns(
                        "/account/add/**"       /* 사용자 등록 */
                        , "/account/all"        /* 사용자 목록 조회 */
                        , "/auth/login/**"      /* 사용자 로그인 */
                        , "/account/update/**"  /* 사용자 정보 수정 */
                );
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
