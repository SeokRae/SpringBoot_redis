package com.sample.domain;

import com.sample.domain.base.TimeEntity;
import com.sample.domain.history.HistoryRefreshToken;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String accessToken;
    private String refreshToken;

    @Builder
    public RefreshToken(String userName, String accessToken, String refreshToken) {
        this.userName = userName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void updateToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public HistoryRefreshToken saveHistory(RefreshToken refreshToken) {
        return HistoryRefreshToken.builder()
                .userName(refreshToken.getUserName())
                .accessToken(refreshToken.getAccessToken())
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
