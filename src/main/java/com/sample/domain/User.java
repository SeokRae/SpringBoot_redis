package com.sample.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class User implements Serializable {

    private String id;
    private String name;
    private int salary;

    @Builder
    public User(String id, String name, int salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}
