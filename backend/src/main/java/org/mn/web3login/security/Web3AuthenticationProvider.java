package org.mn.web3login.security;

import java.util.Objects;

import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class Web3AuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public Web3AuthenticationProvider() {

    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        
        Web3Credentials credentials = web3Authentication.getCredentials();

        // Try to parse the message. Throws an exception if message is not a valid EIP-4361 message.
        SiweMessage siweMessage = null;
        try {
            siweMessage = new SiweMessage(credentials.getMessage());
        } catch (SiweException e) {
            throw new BadCredentialsException("Malformed message!");
        }

        String address = siweMessage.getMAddress();
        
        // Throws UsernameNotFoundException
        UserDetails userDetails = userDetailsService.loadUserByUsername(address);

        // Try to validate signature. Throws an exception if signature is invalid, mandatory fields
        // are missing, expiration has been reached or now < notBefore
        try {
            siweMessage.validate(credentials.getSignature());
        } catch (SiweException e) {
            switch (e.getMErrorType()) {
                case INVALID_SIGNATURE:
                    throw new BadCredentialsException("Invalid signature!");
                case EXPIRED_MESSAGE:
                    throw new CredentialsExpiredException("Expiration time is in the past!");
                case MALFORMED_SESSION:
                    throw new BadCredentialsException("Malformed session!");
                case NOTBEFORE_MESSAGE:
                    throw new LockedException("Account is allowed to authenticate before: " +
                            siweMessage.getMNotBefore());
                default:
                    throw new BadCredentialsException("Unknown credentials error!");
            }
        }

        // Check nonce
        if (!Objects.equals(credentials.getNonce(), siweMessage.getMNonce())) {
            throw new BadCredentialsException("Invalid nonce");
        }

        Web3Principal principal = new Web3Principal(address, userDetails);

        return new Web3AuthenticationToken(principal, credentials, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Web3AuthenticationToken.class.isAssignableFrom(authentication));
    }

}

