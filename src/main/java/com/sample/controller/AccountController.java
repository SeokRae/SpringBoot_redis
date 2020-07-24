package com.sample.controller;

import com.sample.domain.Account;
import com.sample.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/add/{name}/{pw}")
    public void add(@PathVariable("name") final String name, @PathVariable("pw") final String pw) {
        accountService.add(name, pw);
    }

    @GetMapping("/update/{name}")
    public Account update(@PathVariable("name") final String name) {
        return accountService.update(name);
    }

    @GetMapping("/all")
    public List<Account> all() {
        return accountService.list();
    }

    @GetMapping("/redirect")
    public String redirect() {
        return "{\"url\":\"redirect\"}";
    }
}