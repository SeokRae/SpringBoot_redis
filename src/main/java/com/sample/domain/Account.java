package com.sample.domain;

import com.sample.domain.base.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * 해당 클래스의 인스턴스가 레이스에 적재될 때 @RedisHash의 인수를 키로 해당 인스턴스를 값으로 적재
 */
@Entity
@Getter
@NoArgsConstructor
@ToString
public class Account extends TimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String userPw;
    private String role;

    @Builder
    public Account(String userName, String userPw) {
        this.userName = userName;
        this.userPw = userPw;
        this.role = "USER";
    }

    public void update(String name) {
        this.userName = name;
    }
}
