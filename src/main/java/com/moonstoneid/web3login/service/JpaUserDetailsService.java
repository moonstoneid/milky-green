package com.moonstoneid.web3login.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.moonstoneid.web3login.model.Client;
import com.moonstoneid.web3login.model.User;
import com.moonstoneid.web3login.repo.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        List<GrantedAuthority> authorities = user.get().getAuthorities().stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(username, "-", authorities);
    }

    public User findByUsername(String username) {
        Assert.hasText(username, "username cannot be empty");
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void delete(User user) {
        Assert.notNull(user, "user cannot be null");
        userRepository.deleteById(user.getUsername());
    }

    public void save(User user) {
        Assert.notNull(user, "user cannot be null");
        userRepository.save(user);
    }

}
