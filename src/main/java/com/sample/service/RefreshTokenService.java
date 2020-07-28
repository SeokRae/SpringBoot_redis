package com.sample.service;

import com.sample.component.utils.JwtConst;
import com.sample.domain.RefreshToken;
import com.sample.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public String getRefreshTokenByUserName(String userName) {
        return refreshTokenRepository.findTopByUserNameOrderByCreatedAtDesc(userName)
                .map(RefreshToken::getRefreshToken)
                .orElseGet(null);
    }

    @Transactional
    public void add(String userName, String refreshToken) {

        Optional<RefreshToken> optional = refreshTokenRepository.findTopByUserNameOrderByCreatedAtDesc(userName);

        if(optional.isPresent()) {
            RefreshToken oldRefreshToken = optional.get();
            if(oldRefreshToken.getUpdatedAt().isAfter(LocalDateTime.now())) {
                log.info("[RefreshToken] 기존 리프레시 토큰의 updatedAt 시간이 현 시간 보다 길기 때문에 현 시간으로 수정 후 리프레시 토큰 재발급");
                oldRefreshToken.lastUpdateDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        }

        /* 생성된 리플레시 토큰을 DB에 적재 */
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userName(userName)
                        .updatedAt(LocalDateTime.now()
                                .plusMinutes(JwtConst.REFRESH_EXPIRED)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime())
                        .refreshToken(refreshToken)
                        .build());
    }

    @Transactional
    public void update(String userName) {
        refreshTokenRepository.findTopByUserNameOrderByCreatedAtDesc(userName)
                .map(refreshToken -> {
                    refreshToken.lastUpdateDate(
                            LocalDateTime.now().plusMinutes(JwtConst.REDIS_EXPIRED)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    );
                    return refreshToken.getRefreshToken();
                })
                .orElseThrow(() -> new RuntimeException("토큰 값이 존재하지 않음"));
    }
}
