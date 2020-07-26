package com.sample.repository;

import com.sample.domain.history.HistoryRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRefreshTokenRepository extends JpaRepository<HistoryRefreshToken, Long> {
}
