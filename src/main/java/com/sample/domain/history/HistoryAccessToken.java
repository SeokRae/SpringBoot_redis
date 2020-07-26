package com.sample.domain.history;

import com.sample.domain.base.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class HistoryAccessToken extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessToken;
    private String userName;

    @Builder
    public HistoryAccessToken(String accessToken, String userName) {
        this.accessToken = accessToken;
        this.userName = userName;
    }
}
