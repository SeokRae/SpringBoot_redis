package com.sample.controller;

import com.sample.domain.User;
import com.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/add/{id}/{name}")
    public User add(@PathVariable("id") final String id, @PathVariable("name") final String name) {
        return userService.add(User.builder()
                .id(id)
                .name(name)
                .salary(1000)
                .build());
    }

    @GetMapping("/update/{id}/{name}")
    public User update(@PathVariable("id") final String id, @PathVariable("name") final String name) throws Exception {
        return userService.update(id, name);
    }

    @GetMapping("/all")
    public Object all() {
        return userService.findAll();
    }
}
