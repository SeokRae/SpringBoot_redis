package com.sample.service;

import com.sample.domain.Account;
import com.sample.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public void add(String userName, String userPw) {
        Account account = Account.builder().userName(userName).userPw(userPw).build();
        log.info("add Account : {}", account);
        accountRepository.save(account);
    }

    public Account update(String userName) {
        Account account = accountRepository.findByUserName(userName);
        account.update(userName);
        log.info("update Account : {}", account);
        return account;
    }

    /* 등록된 사용자 목록 조회 */
    @Transactional(readOnly = true)
    public List<Account> list() {
        return accountRepository.findAll();
    }

    /* 사용자 명, 비밀번호로 정보 조회 */
    @Transactional(readOnly = true)
    public Account get(String name, String pw) {
        return accountRepository.findByUserNameAndUserPw(name, pw);
    }

    /* 사용자 명으로 사용자 조회 */
    @Transactional(readOnly = true)
    public Account getAccountByUserName(String userName) {
        return accountRepository.findByUserName(userName);
    }
}
