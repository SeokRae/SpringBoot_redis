package com.sample.domain.history;

import com.sample.domain.base.TimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryAccessToken extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String accessToken;

    @Builder
    public HistoryAccessToken(String userName, String accessToken) {
        this.userName = userName;
        this.accessToken = accessToken;
    }
}
