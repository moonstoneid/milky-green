package com.moonstoneid.web3login.service;

import java.util.List;

import com.moonstoneid.web3login.model.User;
import com.moonstoneid.web3login.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void save(User user) {
        Assert.notNull(user, "user cannot be null");
        userRepository.save(user);
    }

    public void delete(User user) {
        Assert.notNull(user, "user cannot be null");
        userRepository.deleteById(user.getUsername());
    }

    public User findByUsername(String username) {
        Assert.hasText(username, "username cannot be empty");
        return userRepository.findByUsername(username).orElse(null);
    }

}
