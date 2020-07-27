package com.sample.repository;

import com.sample.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUserName(String userName);
    Optional<Account> findByUserNameAndUserPw(String userName, String userPw);
}
