package com.sample.service;

import com.sample.domain.User;
import com.sample.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User add(User user) {
        return userRepository.save(User.builder()
                .id(user.getId())
                .name(user.getName())
                .salary(user.getSalary())
                .build());
    }

    @Transactional
    public User update(final String id, final String name) throws Exception {
        return userRepository.findByIdAndName(id, name)
                .map(o -> {
                    o.updatedSalary(2000);
                    return o;
                })
                .orElseThrow(Exception::new);

    }
    public Object findAll() {
        return userRepository.findAll();
    }
}
