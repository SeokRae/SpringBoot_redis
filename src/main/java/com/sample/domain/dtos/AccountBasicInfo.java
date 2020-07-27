package com.sample.domain.dtos;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountBasicInfo {
    private String userName;
    private String role;

    @Builder
    public AccountBasicInfo(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }
}
