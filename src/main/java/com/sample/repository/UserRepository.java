package com.sample.repository;

import com.sample.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByIdAndName(String id, String name);
}
