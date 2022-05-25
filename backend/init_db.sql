INSERT INTO users (username, password, enabled)
  VALUES ('0x76384DEC5e05C2487b58470d5F40c3aeD2807AcB', '', 1);

INSERT INTO authorities (username, authority)
  VALUES ('0x76384DEC5e05C2487b58470d5F40c3aeD2807AcB', 'user');

INSERT INTO oauth2_registered_client (id, client_id, client_id_issued_at, client_secret,
    client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types,
    redirect_uris, scopes, client_settings, token_settings)
  VALUES (
    '76bd89b0-915d-4b35-b440-f5301d6512cc',
    'test-client',
    '2022-03-31 00:51:59.082',
    '{noop}test-secret',
    NULL,
    '76bd89b0-915d-4b35-b440-f5301d6512cc',
    'client_secret_basic',
    'refresh_token,authorization_code',
    'http://127.0.0.1:8080/authorized',
    'openid,message.read,message.write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
);