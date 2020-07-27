package com.sample.domain.history;

import com.sample.domain.base.TimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryRefreshToken extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String userName;

    /* 굳이 오버라이딩 안하고 필드 만들어서 집어 넣어도 됨 */
    @AttributeOverride(name = "createdAt", column = @Column(name = "createdAt"))
    private LocalDateTime createdAt;

    /* 상기 동일 - 일단 데이터 쌓고 있어서 오버라이딩해서 사용 중 */
    @AttributeOverride(name = "updatedAt", column = @Column(name = "updatedAt"))
    private LocalDateTime updatedAt;

    @Builder
    public HistoryRefreshToken(String accessToken, String refreshToken, String userName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
