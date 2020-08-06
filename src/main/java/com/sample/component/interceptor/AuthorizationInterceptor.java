package com.sample.component.interceptor;

import com.sample.component.utils.Constant;
import com.sample.component.utils.JwtUtils;
import com.sample.component.utils.RedisUtils;
import com.sample.domain.Account;
import com.sample.domain.dtos.AccountBasicInfo;
import com.sample.service.AccountService;
import com.sample.service.HistoryAccessTokenService;
import com.sample.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    HistoryAccessTokenService historyAccessTokenService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    public RedisUtils redisUtils;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[Authorization] Process -> Start");
        /* 요청 헤더에서 Authorization 값 추출 및 Bearer prefix 처리 -> accessToken 추출 */
        String accessToken = request.getHeader("Authorization").replace("Bearer ", "");
        String refreshToken = request.getHeader("refresh_token");

        log.info("[JWT InvalidCheck] Request URL : {}", request.getRequestURL());

        try {
            /* resources 접근에 대한 인가 / 권한 처리를 위한 AccessToken 유효성 검사 */
            if(jwtUtils.isValidToken(accessToken)) {

                String signature = accessToken.split(Constant.JwtConst.SPLIT_TOKEN_SEPARATOR)[2];
                if(redisUtils.hasKey(Constant.RedisConst.PREFIX_KEY + signature, accessToken)) {
                    /* redis에 accessToken이 있는지 확인 해야 함 */
                    log.info("[JWT Invalid]");
                    return true;
                } else {
                    /* 토큰은 유효하나 Redis에 존재하고 있지 않음 */
                    log.error("[JWT RedisServer Check]");
                }
            }
        } catch (ExpiredJwtException e) {

            try {
                if(jwtUtils.isValidToken(refreshToken)) {
                    /* RefreshToken이 유효함으로 써 AccessToken 재발급 프로세스 진행 */
                    String newAccessToken = reIssueAccessToken(refreshToken);
                    response.addHeader(Constant.JwtConst.AUTHORIZATION, Constant.JwtConst.BEARER + newAccessToken);
                    response.addHeader(Constant.JwtConst.REFRESH_TOKEN, refreshToken);

                    return true;
                }
            } catch (ExpiredJwtException refreshTokenException) {
                log.error("[JWT ExpiredJwtException] RefreshToken 유효기간 만료로 로그인 필요 ");
            }
        }
        return false;
    }

    /**
     * RefreshToken으로 AccessToken 재발급 로직
     * @param refreshToken DB에 등록된 refreshToken
     * @return 유효한 RefreshToken으로 AccessToken을 발급하는 프로세스
     */
    private String reIssueAccessToken(String refreshToken) {

        String userName = jwtUtils.getUserNameFromToken(refreshToken);
        /* refreshToken의 Payload에 있는 사용자를 DB 조회 */
        Account account = accountService.getAccountByUserName(userName);
        log.debug("[JWT ExpiredJwtException] AccessToken을 재발행하기 위한 사용자 정보 조회 : {}", account);

        /* 조회된 사용자 정보를 통해 AccessToken 재발급 */
        AccountBasicInfo accountBasicInfo = account.toEntity();
        String newAccessToken = jwtUtils.generateToken(accountBasicInfo, Constant.RedisConst.ACCESS_EXPIRED);

        log.debug("[JWT ExpiredJwtException] 새로운 AccessToken 발행 : {}", newAccessToken);

        String signature = newAccessToken.split(Constant.JwtConst.SPLIT_TOKEN_SEPARATOR)[2];
        historyAccessTokenService.add(signature, userName, newAccessToken); /* 엑세스 토큰 이력 임시저장 */
        /* AccessToken Redis 등록 및 유효시간 설정 */
        redisUtils.makeRefreshTokenAndExpiredAt(signature, newAccessToken, accountBasicInfo);

        return newAccessToken;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }
}
