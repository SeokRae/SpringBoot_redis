package com.sample.component.interceptor;

import com.sample.component.utils.Constant;
import com.sample.component.utils.JwtUtils;
import com.sample.component.utils.RedisUtils;
import com.sample.component.utils.StringUtils;
import com.sample.domain.Account;
import com.sample.domain.dtos.AccountBasicInfo;
import com.sample.service.AccountService;
import com.sample.service.HistoryAccessTokenService;
import com.sample.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.sample.component.utils.Constant.JwtConst.BEARER;
import static com.sample.component.utils.Constant.JwtConst.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HistoryAccessTokenService historyAccessTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(!"POST".equalsIgnoreCase(request.getMethod())) {
            // method 방식이 POST가 아닌 경우 유효하지 않은 요청 방식
            log.error("POST 메서드 요청이 아니면 토큰을 발행 받을 수 없음");
            return false;
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        assert modelAndView != null;

        // 사용자 조회
        Map<String, Object> modelMap = modelAndView.getModel();
        AccountBasicInfo accountBasicInfo = (AccountBasicInfo) modelMap.get("accountBasicInfo");

        /* 엑세스 토큰, 리프레시 토큰 발급 */
        String accessToken = createTokens(Constant.JwtConst.ACCESS_TOKEN, accountBasicInfo);
        String refreshToken = createTokens(REFRESH_TOKEN, accountBasicInfo);

        response.addHeader(AUTHORIZATION, BEARER + accessToken);
        response.addHeader(REFRESH_TOKEN, refreshToken);
        response.addHeader("grant_type", accountBasicInfo.getRole());
    }

    /**
     * 엑세스 / 리프레시 토큰 발급
     * 1. 엑세스 토큰 발환
     * 2. 리프레시 토큰 DB 저장
     */
    private String createTokens(String tokenType, AccountBasicInfo accountBasicInfo) {
        log.info("============ [Authentication Create Tokens] Start ============");
        String userName = accountBasicInfo.getUserName();
        String token = "";
        // 엑세스 토큰 발급 및 헤더 저장 & 리프레시 토큰 발급
        if(Constant.JwtConst.ACCESS_TOKEN.equals(tokenType)) {
            token = jwtUtils.generateToken(accountBasicInfo, Constant.RedisConst.ACCESS_EXPIRED);

            String signature = token.split(Constant.JwtConst.SPLIT_TOKEN_SEPARATOR)[2];
            redisUtils.makeRefreshTokenAndExpiredAt(signature, token, accountBasicInfo);
            historyAccessTokenService.add(signature, userName, token);

        } else if(REFRESH_TOKEN.equals(tokenType)) {
            token = jwtUtils.generateToken(accountBasicInfo, Constant.RedisConst.REFRESH_EXPIRED);
            refreshTokenService.add(userName, token);
        }
        log.info("============ [Authentication Create Tokens] End ============");
        return token;
    }
}
