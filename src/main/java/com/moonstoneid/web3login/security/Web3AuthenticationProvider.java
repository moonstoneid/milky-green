package com.moonstoneid.web3login.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import com.moonstoneid.web3login.model.User;
import com.moonstoneid.web3login.service.SettingService;
import com.moonstoneid.web3login.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class Web3AuthenticationProvider implements AuthenticationProvider {

    private SettingService settingService;
    private UserService userService;

    public Web3AuthenticationProvider() {

    }

    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Credentials credentials = web3Authentication.getCredentials();

        // Try to parse the message
        // Throws an exception if message is not a valid EIP-4361 message.
        SiweMessage siweMessage;
        try {
            siweMessage = new SiweMessage.Parser().parse(credentials.getMessage());
        } catch (SiweException e) {
            throw new BadCredentialsException("Malformed message!");
        }

        String address = siweMessage.getAddress();

        // Try to validate signature
        // Throws an exception if signature is invalid, mandatory fields are missing, expiration has
        // been reached or now < notBefore
        try {
            // TODO: fix domain check
            siweMessage.verify(credentials.getDomain(), credentials.getNonce(), credentials.getSignature());
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
            user.setAuthorities(new ArrayList<>());
            userService.save(user);
        }
        // Check if user was found
        if (user == null) {
            throw new UsernameNotFoundException(address);
        }

        UserDetails userDetails = createUserDetails(user);
        Web3Principal principal = new Web3Principal(address, userDetails);

        return new Web3AuthenticationToken(principal, userDetails.getAuthorities());
    }

    private static UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), "-",
                authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Web3AuthenticationToken.class.isAssignableFrom(authentication));
    }

}
