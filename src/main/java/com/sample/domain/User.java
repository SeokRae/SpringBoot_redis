package com.sample.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@RedisHash("user")
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    private String id;
    private String name;
    private int salary;

    @Builder
    public User(String id, String name, int salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public void updatedSalary(int salary) {
        this.salary = salary;
    }
}
