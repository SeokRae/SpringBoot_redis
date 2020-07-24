package com.sample.service;

import com.sample.domain.RefreshToken;
import com.sample.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /* accessToken 만료 시 refreshToken 확인해서 재발급 하기 위한 메서드 */
    @Transactional(readOnly = true)
    public String getRefreshTokenByUserName(String userName) {
        return refreshTokenRepository.findByUserName(userName)
                .map(RefreshToken::getRefreshToken)
                .orElseGet(() -> {throw new RuntimeException();});
    }

    /* refreshToken 발급 시 DB 저장 */
    @Transactional
    public void add(String userName , String refreshToken) {
        /* 생성된 리플레시 토큰을 DB에 적재 */
        RefreshToken reToken = RefreshToken.builder()
                .userName(userName)
                .refreshToken(refreshToken)
                .build();

        /* 리플레시토큰 저장 */
        refreshTokenRepository.save(reToken);
    }
}
