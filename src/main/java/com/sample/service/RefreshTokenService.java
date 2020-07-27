package com.sample.service;

import com.sample.domain.RefreshToken;
import com.sample.repository.HistoryRefreshTokenRepository;
import com.sample.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private HistoryRefreshTokenRepository historyRefreshTokenRepository;

    @Transactional(readOnly = true)
    public String getRefreshTokenByUserName(String userName) {
        return refreshTokenRepository.findTopByUserNameOrderByCreatedAt(userName)
                .map(RefreshToken::getRefreshToken)
                .orElseGet(null);
    }

    @Transactional
    public void add(String userName, String refreshToken) {

        Optional<RefreshToken> optional = refreshTokenRepository.findTopByUserNameOrderByCreatedAt(userName);

        if(optional.isPresent()) {
            RefreshToken oldRefreshToken = optional.get();
            /* 기존 refreshToken 마지막 수정 시간 변경 */
            oldRefreshToken.lastUpdateDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        /* 생성된 리플레시 토큰을 DB에 적재 */
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userName(userName)
                        .refreshToken(refreshToken)
                        .build());
    }

    @Transactional
    public void update(String userName) {
        refreshTokenRepository.findByUserName(userName)
                .map(refreshToken -> {
                    refreshToken.lastUpdateDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    return refreshToken.getRefreshToken();
                })
                .orElseThrow(() -> new RuntimeException("토큰 값이 존재하지 않음"));
    }
}
