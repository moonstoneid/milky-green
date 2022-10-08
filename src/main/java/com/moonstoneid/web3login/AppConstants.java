package com.moonstoneid.web3login;

public interface AppConstants {
    String DOMAIN = "localhost:9000";

    String API_KEY_HEADER = "X-API-KEY";
    String API_KEY_PRINCIPAL = "API_KEY";
    String API_KEY_ROLE = "ROLE_API_ACCESS";

    String OAUTH_SCOPE_OPENID = "openid";
    String OAUTH_SCOPE_PROFILE = "profile";
    String OAUTH_SCOPE_EMAIL = "email";
    String OAUTH_SCOPE_READ = "message.read";
    String OAUTH_SCOPE_WRITE = "message.write";
}
