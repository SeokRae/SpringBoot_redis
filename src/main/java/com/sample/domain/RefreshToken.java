package com.sample.domain;

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
public class RefreshToken extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String refreshToken;

    /* 상기 동일 - 일단 데이터 쌓고 있어서 오버라이딩해서 사용 중 */
    @AttributeOverride(name = "updatedAt", column = @Column(name = "updatedAt"))
    private LocalDateTime updatedAt;

    @Builder
    public RefreshToken(String userName, String refreshToken, LocalDateTime updatedAt) {
        this.userName = userName;
        this.refreshToken = refreshToken;
        this.updatedAt = updatedAt;
    }

    public void lastUpdateDate(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
