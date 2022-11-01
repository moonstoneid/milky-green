package com.moonstoneid.web3login.security;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Web3SecurityJackson2Module extends SimpleModule {

    public Web3SecurityJackson2Module() {
        super(Web3SecurityJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Web3Principal.class, Web3PrincipalMixin.class);
        context.setMixInAnnotations(Web3Credentials.class, Web3CredentialsMixin.class);
        context.setMixInAnnotations(Web3AuthenticationToken.class, Web3AuthenticationTokenMixin.class);
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
    public static abstract class Web3PrincipalMixin extends Web3Principal {

        public Web3PrincipalMixin(@JsonProperty("name") String name,
                @JsonProperty("userDetails") UserDetails userDetails) {
            super(name, userDetails);
        }

    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
    public static abstract class Web3CredentialsMixin extends Web3Credentials {

        public Web3CredentialsMixin(@JsonProperty("message") String message,
                @JsonProperty("signature") String signature,
                @JsonProperty("nonce") String nonce) {
            super(message, signature, nonce);
        }

    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static abstract class Web3AuthenticationTokenMixin extends Web3AuthenticationToken {

        public Web3AuthenticationTokenMixin(@JsonProperty("principal") Web3Principal principal,
                @JsonProperty("credentials") Web3Credentials credentials,
                @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                @JsonProperty("authenticated") boolean isAuthenticated) {
            super(principal, credentials, authorities, isAuthenticated);
        }

    }

}
