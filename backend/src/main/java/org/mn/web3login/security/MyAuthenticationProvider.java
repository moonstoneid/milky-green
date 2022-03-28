package org.mn.web3login.security;

import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class MyAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public MyAuthenticationProvider() {

    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String[] credentials = (String[]) authentication.getCredentials();

        String message = credentials[0];
        String signature = credentials[1];
        String nonce = credentials[2];

        SiweMessage siweMessage = null;
        UserDetails userDetails = null;
        String address = null;

        try {
            // Try to parse the String. Throws an exception if message is not a valid EIP-4361 message.
            siweMessage = new SiweMessage(message);
            address = siweMessage.getMAddress();

            // Throws UsernameNotFoundException
            userDetails = userDetailsService.loadUserByUsername(address);

            // Validate signature. Throws an exception if signature is invalid,
            // mandatory fields are missing, expiration has been reached or now < notBefore
            siweMessage.validate(signature);

        } catch (SiweException e) {
            switch (e.getMErrorType()) {
                case INVALID_SIGNATURE:
                    throw new BadCredentialsException("Invalid Credentials");
                case EXPIRED_MESSAGE:
                    throw new CredentialsExpiredException("expirationTime is in the past");
                case MALFORMED_SESSION:
                    throw new BadCredentialsException("Malformed session, some required fields are missing");
                case MALFORMED_MESSAGE:
                    throw new BadCredentialsException("Malformed message, some required fields are missing");
                case NOTBEFORE_MESSAGE:
                    throw new LockedException("Account is allowed to authenticate before "+ siweMessage.getMNotBefore());
                default:
                    throw new BadCredentialsException("Unknown credentials error");
            }
        }

        // Check nonce
        if(!nonce.equals(siweMessage.getMNonce())){
            throw new BadCredentialsException("Invalid nonce");
        }

        return new UsernamePasswordAuthenticationToken(address, signature,
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}

