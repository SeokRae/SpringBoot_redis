package com.sample.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountBasicInfo {
    private String userName;
    private String role;

    @Builder
    public AccountBasicInfo(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }
}
