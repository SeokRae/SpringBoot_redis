package com.sample.controller;

import com.sample.domain.User;
import com.sample.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/add/{id}/{name}")
    public User add(@PathVariable("id") final String id, @PathVariable("name") final String name) {
        userRepository.save(
                User.builder()
                        .id(id)
                        .name(name)
                        .salary(1000)
                        .build());
        return userRepository.findById(id);
    }

    @GetMapping("/update/{id}/{name}")
    public User update(@PathVariable("id") final String id, @PathVariable("name") final String name) {
        userRepository.save(
                User.builder()
                        .id(id)
                        .name(name)
                        .salary(2000)
                        .build());
        return userRepository.findById(id);
    }

    @GetMapping("/all")
    public Map<String, Object> all() {
        return userRepository.findAll();
    }
}
