package com.sample.repository;

import com.sample.domain.User;

import java.util.Map;

public interface UserRepository {
    void save(User account);
    Map<String, Object> findAll();
    User findById(String id);
    void update(User account);
    void delete(String id);
}
