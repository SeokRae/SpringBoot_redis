package com.sample.repository;

import com.sample.domain.history.HistoryAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryAccessTokenRepository extends JpaRepository<HistoryAccessToken, Long> {
}
