package com.sample.repository;

import com.sample.domain.User;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final RedisTemplate<String, User> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    public UserRepositoryImpl(RedisTemplate<String, User> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(User user) {
        hashOperations.put("USER", user.getId(), user);
    }

    @Override
    public Map<String, Object> findAll() {
        redisTemplate.getClientList().forEach(
                redisClientInfo -> {
                    System.out.println("client : " + redisClientInfo);
                }
        );
        return hashOperations.entries("USER");
    }

    @Override
    public User findById(String id) {
        return (User) hashOperations.get("USER", id);
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(String id) {
        hashOperations.delete("USER", id);
    }
}
