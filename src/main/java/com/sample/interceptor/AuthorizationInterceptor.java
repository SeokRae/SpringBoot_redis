package com.sample.interceptor;

import com.sample.domain.Account;
import com.sample.domain.dtos.AccountBasicInfo;
import com.sample.service.AccountService;
import com.sample.service.RefreshTokenService;
import com.sample.utils.JwtConst;
import com.sample.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Account> hashOperations;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Authorization 처리");
        // Token 확인 -> AccessToken
        String accessToken = request.getHeader("Authorization")
                .replace("Bearer ", "");

        if(!jwtUtils.isValidToken(accessToken)) {
            log.error("[JWT ExpiredJwtException] 발생 -> AccessToken 재발행 프로세스 시작 !!");

            /* Redis에 토큰 존재 여부 확인 */
            if(hashOperations.hasKey("user", accessToken)) {
                log.error("[JWT ExpiredJwtException] redis에 accessToken이 존재");
                Account account = hashOperations.get("user", accessToken);

                String redisToken = accessToken;
                String redisUserName = account.getUserName();

                String refreshToken = refreshTokenService.getRefreshTokenByUserName(redisUserName);
                log.error("[JWT ExpiredJwtException] DB 조회하여 RefreshToken 조회 후 유효성 검사 : {}", refreshToken);

                if(jwtUtils.isValidToken(refreshToken)) {
                    log.error("[JWT ExpiredJwtException] RefreshToken 유효성 확인 -> AccessToken 재발급 시작");
                    String newAccessToken = reAccessToken(refreshToken);
                    response.addHeader("Authorization", "Bearer " + newAccessToken);

                } else {
                    /* 리프레시 토큰의 오류인 경우 -> 로그인 필요 */
                    log.error("[JWT RefreshTokenException] 리프레시 토큰 만료 -> 자원 접근 불가 (로그인 필요)");
                    return false;
                }
            } else {
                /* Redis에 토큰이 존재 하지 않음 (Redis 서버의 문제일 수 있고, 만료기간이 지난 토큰일 수 있고, 탈취된 토큰 일 수 있다.) */
                log.error("토큰이 redis에 없음");
            }
        }

        log.info("권한에 따른 로직 필요");
        return true;
    }

    /* AccessToken 재발급 로직 */
    private String reAccessToken(String refreshToken) {

        String userName = jwtUtils.getUserNameFromToken(refreshToken);
        Account account = accountService.getAccountByUserName(userName);
        AccountBasicInfo accountBasicInfo = AccountBasicInfo.builder()
                .userName(account.getUserName())
                .role(account.getRole())
                .build();
        log.error("[JWT ExpiredJwtException] AccessToken을 재발행하기 위한 사용자 정보 조회 : {}", account);

        String newAccessToken = jwtUtils.generateToken(accountBasicInfo, JwtConst.ACCESS_EXPIRED);
        log.error("[JWT ExpiredJwtException] 새로운 AccessToken 발행 : {}", newAccessToken);
        hashOperations.put("user", newAccessToken, account);
        redisTemplate.expireAt("user", Date.from(LocalDateTime.now().plusMinutes(JwtConst.ACCESS_EXPIRED).atZone(ZoneId.systemDefault()).toInstant()));

        /* accessToken 재갱신 */
        return newAccessToken;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
}
