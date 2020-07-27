package com.sample.repository;

import com.sample.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserName(String userName);
    Optional<RefreshToken> findTopByUserNameOrderByCreatedAtDesc(String userName);
}
