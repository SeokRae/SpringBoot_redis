package com.sample.service;

import com.sample.domain.history.HistoryAccessToken;
import com.sample.repository.HistoryAccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryAccessTokenService {
    @Autowired
    private HistoryAccessTokenRepository historyAccessTokenRepository;

    public void add(String signature, String userName, String accessToken) {
        historyAccessTokenRepository.save(HistoryAccessToken.builder()
                .userName(userName)
                .signature(signature)
                .accessToken(accessToken)
                .build());
    }
}
