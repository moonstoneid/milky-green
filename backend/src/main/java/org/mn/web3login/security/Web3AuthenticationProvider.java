package org.mn.web3login.security;

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
        String domain = credentials.getDomain();

        // Try to parse the message
        // Throws an exception if message is not a valid EIP-4361 message.
        SiweMessage siweMessage = null;
        try {
            siweMessage = new SiweMessage.Parser().parse(credentials.getMessage());
        } catch (SiweException e) {
            throw new BadCredentialsException("Malformed message!");
        }

        String address = siweMessage.getAddress();

        // Throws UsernameNotFoundException
        UserDetails userDetails = userDetailsService.loadUserByUsername(address);

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

        Web3Principal principal = new Web3Principal(address, userDetails);
        return new Web3AuthenticationToken(principal, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Web3AuthenticationToken.class.isAssignableFrom(authentication));
    }

}
