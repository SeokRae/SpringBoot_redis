package com.sample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    /* 인터셉터에서 경로 설정하고 preHandle 에서 return 값을 false로 해서 컨트롤러 까지 안옴 */
    @PostMapping("/login")
    public void login() {
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok().body("{\"role\":\"user\", \"data\": [{\"id\": 1, \"name\": \"item1\"}, {\"id\": 2, \"name\": \"item2\"}]}");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> getAdmin() {
        return ResponseEntity.ok().body("{\"role\": \"admin\", \"data\": [{\"id\": 1, \"name\": \"item1\", \"author\": \"user1\"}, {\"id\": 2, \"name\": \"item2\", \"author\": \"user2\"}]}");
    }
}
