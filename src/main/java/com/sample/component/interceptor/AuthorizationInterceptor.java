package com.sample.component.interceptor;

import com.sample.component.utils.JwtConst;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
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

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, AccountBasicInfo> hashOperations;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[Authorization] Process -> Start");
        /* 요청 헤더에서 Authorization 값 추출 및 Bearer prefix 처리 -> accessToken 추출 */
        String accessToken = request.getHeader("Authorization").replace("Bearer ", "");

        log.info("[JWT InvalidCheck] Request URL : {}", request.getRequestURL());
        /* resources 접근에 대한 인가 / 권한 처리를 위한 AccessToken 유효성 검사 */
        try {
            if(jwtUtils.isValidToken(accessToken)) {
                log.info("[JWT Invalid]");
                return true;
            }
        } catch (ExpiredJwtException e) {

            log.error("[JWT ExpiredJwtException] 발생 -> AccessToken 재발행 프로세스 시작 !!");
            String userNameInToken = (String) e.getClaims().get("id");
            log.info("[JWT ExpiredJwtException] Exception의 계정정보 {}", userNameInToken);
            String signKey = JwtConst.PREFIX_KEY + userNameInToken;

            /* Redis 내에 Token 확인 */
            boolean redisHasToken = redisUtils.hasKey(signKey, accessToken);

            if(redisHasToken) {
                /* Redis의 Token 값 확인 후 RefreshToken에 접근 할 데이터 조회 */
                AccountBasicInfo accountBasicInfo = redisUtils.get(signKey, accessToken);
                log.error("[JWT ExpiredJwtException] redis에 accessToken이 존재");

                /* Redis에 존재하는 토큰의 Account 객체의 값으로 refreshToken 검색 -> 후 AccessToken 재발급 */
                assert accountBasicInfo != null;
                String userNameInHashValue = accountBasicInfo.getUserName();

                String refreshToken = refreshTokenService.getRefreshTokenByUserName(userNameInHashValue);
                log.error("[JWT ExpiredJwtException] DB 조회하여 RefreshToken 조회 후 유효성 검사 : {}", refreshToken);

                if(jwtUtils.isValidToken(refreshToken)) {
                    log.error("[JWT ExpiredJwtException] RefreshToken 유효성 확인 -> AccessToken 재발급 시작");
                    String newAccessToken = reAccessToken(refreshToken);
                    response.addHeader(JwtConst.AUTHORIZATION, JwtConst.BEARER + newAccessToken);

                } else {
                    /* 리프레시 토큰의 오류인 경우 -> 로그인 필요 */
                    log.error("[JWT RefreshTokenException] 리프레시 토큰 만료 -> 자원 접근 불가 (로그인 필요)");
                    return false;
                }
            } else {
                /* Redis에 토큰이 존재 하지 않음 (Redis 서버의 문제일 수 있고, 탈취된 토큰 일 수 있다.) */
                log.error("[JWT ExpiredJwtException] Redis 내에 AccessToken 유실 -> 원인 파악 필요");
                return false;
            }
        }
        /* resources 접근을 위한 accessToken의 유효성 체크 완료 -> resources에 정상 접근 */
        return false;
    }

    /* AccessToken 재발급 로직 */
    private String reAccessToken(String refreshToken) {
        /* key 값 후보들 */
        String userName = jwtUtils.getUserNameFromToken(refreshToken);
        Account account = accountService.getAccountByUserName(userName);
        log.error("[JWT ExpiredJwtException] AccessToken을 재발행하기 위한 사용자 정보 조회 : {}", account);

        AccountBasicInfo accountBasicInfo = account.toEntity();
        String newAccessToken = jwtUtils.generateToken(accountBasicInfo, JwtConst.ACCESS_EXPIRED);
        log.error("[JWT ExpiredJwtException] 새로운 AccessToken 발행 : {}", newAccessToken);

        /* AccessToken 이력 관리 -> 추후 Redis AOF로 변경 */
        historyAccessTokenService.add(userName, newAccessToken);
        redisUtils.makeRefreshTokenAndExpiredAt(userName, newAccessToken, accountBasicInfo);

        /* accessToken 재갱신 */
        return newAccessToken;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }
}
