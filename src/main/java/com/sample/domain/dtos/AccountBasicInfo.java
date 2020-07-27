package com.sample.domain.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountBasicInfo {
    private String userName;
    private String role;

    @Builder
    public AccountBasicInfo(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }
}
