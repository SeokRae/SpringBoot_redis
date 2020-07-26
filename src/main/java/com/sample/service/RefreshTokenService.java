package com.sample.service;

import com.sample.domain.RefreshToken;
import com.sample.domain.history.HistoryRefreshToken;
import com.sample.repository.HistoryRefreshTokenRepository;
import com.sample.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private HistoryRefreshTokenRepository historyRefreshTokenRepository;

    @Transactional(readOnly = true)
    public String getRefreshTokenByAccessToken(String accessToken) {
        return refreshTokenRepository.findByAccessToken(accessToken)
                .map(RefreshToken::getRefreshToken)
                .orElseGet(() -> {throw new RuntimeException();});
    }

    @Transactional(readOnly = true)
    public String getRefreshTokenByUserName(String userName) {
        return refreshTokenRepository.findByAccessToken(userName)
                .map(RefreshToken::getRefreshToken)
                .orElseGet(() -> {throw new RuntimeException();});
    }

    @Transactional
    public void add(String userName, String accessToken , String refreshToken) {

        Optional<RefreshToken> optional = refreshTokenRepository.findByUserName(userName);

        if(optional.isPresent()) {
            RefreshToken oldRefreshToken = optional.get();
            HistoryRefreshToken historyRefreshToken = HistoryRefreshToken.builder()
                    .userName(oldRefreshToken.getUserName())
                    .accessToken(oldRefreshToken.getAccessToken())
                    .refreshToken(oldRefreshToken.getRefreshToken())
                    .build();

            /* 기존 데이터 이력 저장 */
            historyRefreshTokenRepository.save(historyRefreshToken);
            /* 기존 데이터 해당 테이블에서 삭제 */
            refreshTokenRepository.delete(oldRefreshToken);
            /* 새로 생성된 refreshToken 등록 */
            refreshTokenRepository.save(RefreshToken.builder()
                    .userName(userName)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build()
            );
        } else {
            /* 생성된 리플레시 토큰을 DB에 적재 */
            RefreshToken reToken = RefreshToken.builder()
                    .userName(userName)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            /* 리플레시토큰 저장 */
            refreshTokenRepository.save(reToken);
        }
    }

    @Transactional
    public String update(String userName, String accessToken) {
        return refreshTokenRepository.findByUserName(userName)
                .map(refreshToken -> {
                    refreshToken.updateToken(accessToken);
                    return refreshToken.getAccessToken();
                })
                .orElseThrow(() -> new RuntimeException("토큰 값이 존재하지 않음"));
    }
}
