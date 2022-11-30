package com.moonstoneid.milkygreen.security;

import java.util.Collections;
import java.util.List;

import com.moonstoneid.milkygreen.eth.SiweMessageVerifier;
import com.moonstoneid.milkygreen.model.User;
import com.moonstoneid.milkygreen.service.SettingService;
import com.moonstoneid.milkygreen.service.UserService;
import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class Web3AuthenticationProvider implements AuthenticationProvider {

    private final SettingService settingService;
    private final UserService userService;

    private final SiweMessageVerifier messageVerifier;

    public Web3AuthenticationProvider(SettingService settingService, UserService userService,
            SiweMessageVerifier messageVerifier) {
        this.settingService = settingService;
        this.userService = userService;
        this.messageVerifier = messageVerifier;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        Web3AuthenticationToken authentication = (Web3AuthenticationToken) auth;
        Web3Credentials credentials = authentication.getCredentials();

        // Try to parse the message
        // Throws an exception if message is not a valid EIP-4361 message
        SiweMessage message;
        try {
            message = messageVerifier.parseMessage(credentials.getMessage());
        } catch (SiweException e) {
            throw new BadCredentialsException("Malformed message!");
        }

        String address = message.getAddress();

        // Try to validate signature
        // Throws an exception if signature is invalid, mandatory fields are missing, expiration has
        // been reached or now < notBefore
        try {
            messageVerifier.verifyMessage(message, credentials.getNonce(), credentials.getSignature());
        } catch (SiweException e) {
            switch (e.getErrorType()) {
                case DOMAIN_MISMATCH:
                    throw new BadCredentialsException("Domain does not match!");
                case NONCE_MISMATCH:
                    throw new CredentialsExpiredException("Nonce does not match!");
                case EXPIRED_MESSAGE:
                    throw new BadCredentialsException("Message expired!");
                case NOT_YET_VALID_MESSAGE:
                    throw new LockedException("Message not valid yet!");
                case INVALID_SIGNATURE:
                    throw new BadCredentialsException("Invalid signature");
                default:
                    throw new BadCredentialsException("Unknown credentials error!");
            }
        }

        // Try to find user
        User user = userService.findByUsername(address);
        // Try to import user
        if (settingService.isAllowAutoImport() && user == null) {
            user = new User();
            user.setUsername(address);
            user.setEnabled(true);
            userService.save(user);
        }
        // Check if user was found
        if (user == null) {
            throw new UsernameNotFoundException(address);
        }
        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled!");
        }

        UserDetails userDetails = createUserDetails(user);
        Web3Principal principal = new Web3Principal(address, userDetails);

        return new Web3AuthenticationToken(principal, userDetails.getAuthorities());
    }

    private static UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.emptyList();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), "-",
                authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Web3AuthenticationToken.class.isAssignableFrom(authentication));
    }

}
