package com.sample.interceptor;

import com.sample.domain.Account;
import com.sample.domain.dtos.AccountBasicInfo;
import com.sample.service.AccountService;
import com.sample.service.RefreshTokenService;
import com.sample.utils.JwtConst;
import com.sample.utils.JwtUtils;
import com.sample.utils.StringUtils;
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
import java.util.Map;

@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, AccountBasicInfo> hashOperations;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!"POST".equalsIgnoreCase(request.getMethod())) {
            // method 방식이 POST가 아닌 경우 유효하지 않은 요청 방식
            log.error("POST 메서드 요청이 아니면 토큰을 발행 받을 수 없음");
        }
        // form 파라미터 확인
        String name = StringUtils.getOrDefault(request.getParameter("userName"), "");
        String pw = StringUtils.getOrDefault(request.getParameter("userPw"), "");

        // 사용자 확인 및 토큰 발급
        Account account = accountService.get(name, pw);
        AccountBasicInfo accountBasicInfo = AccountBasicInfo.builder()
                .userName(account.getUserName())
                .role(account.getRole())
                .build();

        // 여기까지 왔으면 성공 그전에 예외처리가 되어야 함
        ModelAndView model = new ModelAndView();
        model.addObject("accountBasicInfo", accountBasicInfo);

        postHandle(request, response, handler, model);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        assert modelAndView != null;

        // 사용자 조회
        Map<String, Object> modelMap = modelAndView.getModel();
        AccountBasicInfo accountBasicInfo = (AccountBasicInfo) modelMap.get("accountBasicInfo");

        /* 엑세스 토큰, 리프레시 토큰 발급 */
        String accessToken = createTokens(accountBasicInfo);
        response.addHeader("Authorization", "Bearer " + accessToken);

    }

    /**
     * 엑세스 / 리프레시 토큰 발급
     * 1. 엑세스 토큰 발환
     * 2. 리프레시 토큰 DB 저장
     * @param accountBasicInfo request 로 DB 조회한
     * @return
     */
    private String createTokens(AccountBasicInfo accountBasicInfo) {
        // DB 조회 시 오류 발생 처리 필요
        String userName = accountBasicInfo.getUserName();
        // 엑세스 토큰 발급 및 헤더 저장
        String accessToken = jwtUtils.generateToken(accountBasicInfo, JwtConst.ACCESS_EXPIRED);
        // redis에 토큰 생성

        hashOperations.put("user", accessToken, accountBasicInfo);
        redisTemplate.expireAt("user", Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()));

        log.info("redis USER, TOKEN:{}", accessToken);
        // 리프레시 토큰 발급 및 DB 저장
        String refreshToken = jwtUtils.generateToken(accountBasicInfo, JwtConst.REFRESH_EXPIRED);
        refreshTokenService.add(userName, refreshToken);

        return accessToken;
    }
}
