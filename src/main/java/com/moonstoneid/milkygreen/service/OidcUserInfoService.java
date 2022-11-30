package com.moonstoneid.milkygreen.service;

import java.util.Optional;

import com.moonstoneid.milkygreen.model.User;
import com.moonstoneid.milkygreen.model.UserEns;
import com.moonstoneid.milkygreen.repo.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

@Service
public class OidcUserInfoService {

    private final UserRepository userRepository;

    public OidcUserInfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public OidcUserInfo loadUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        UserEns userEns = user.get().getEns();
        if (userEns == null) {
            return OidcUserInfo.builder()
                    .subject(username)
                    .preferredUsername(username)
                    .build();
        }

        return OidcUserInfo.builder()
                .subject(username)
                .name(userEns.getUsername())
                .preferredUsername(username)
                .website(userEns.getEnsUrl())
                .email(userEns.getEnsEmail())
                .build();
    }

}