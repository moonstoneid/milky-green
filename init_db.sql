INSERT INTO users (username, enabled)
  VALUES ('0x983110309620D911731Ac0932219af06091b6744', 1);

INSERT INTO authorities (username, authority)
  VALUES ('0x983110309620D911731Ac0932219af06091b6744', 'user');

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
    'openid,profile,email,message.read,message.write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.core.OAuth2TokenFormat","value":"self-contained"},"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
);

INSERT INTO jwk_keypair (id, private_key, public_key)
  VALUES (
    '1',
    '-----BEGIN RSA PRIVATE KEY-----MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHG0CkQYs9HgM34oA0QjWWedCQOng3hni3LMg9TRSKzjO1UlLl6KvG8TfnUGyWwonk38MaCpAOYqHy8RlV6+RiZWxd11O/LvlVej20giQTCKEyHCPeR9wzmDx2bKu64/PwsvoVeuW1ohQ90SfPAJAA4gW8yDmWmerZlwMZ3nt/Y0O4P0zou1wpYrNcHmT4gachog0KlodTmCwTY3JYvhFnpE5+N9xgZOTOSHUtCET/q5QmO02d6ShHot4itD8mJUC/wfgq8pKB4Fn+PHjBEN24V0Qc0HLUhto5Sd1P8GasIJuGcVNKtBzKJLiZhEkroKBaRQb4OHuzNAAeiAiaP7BzAgMBAAECggEBAIYQvOxBPfG/vmHOGHnC2XogFp2H55LxlQfvICP0QNYcF6NttWT+pLYc1uRqIdbX5A2BHRDSOnCL3zg7lnB0S4R4MDhOW4PzEOeJDYH5QeLBRXN8CPuhQxjPj+/TlfU2DfJjJ1X5RcgetQdCbMY0QLEfl6Hk7YBL64dPGnypENMuMpyn5iJ+HNFI6m9TyfV0ztCon3n+8NhRtY/PwMVtDM/YdbznuXCaJ81ULGu52AFLc8Rmqm8EDBLMipTV0ZB8x2S1HFe/KmbwZmYiKmyHW8c6PWejJipCN4patGbeB2m8/Pcrv2nWSXAS6Ej/yoniPhAT9uCGUMjIVSU5ohQo4gECgYEAyvHQ88OXF4+R7nBuv1tYz9Nmvxt6KwE9WoryPds0eOWAgHYFfF/ZBLi4bdYRLPQxRff09iQ+Ir8BYnG6c4PNGHv6IyX8lfzu6T8kE/j2EqJVM64OW9vnuBMf7q2SaJkJdQdOxmsH/VqXjpXB9DS1UY12bPUThZBRGyRIUDrSzSECgYEAqm1Ua06BdME4naYgenb24Ksy5T5iApp2OZvSCo3LyJPhddUwffMt9srI1WzLzxi47GXpcb+2cO025HXdBJQl3V7YnQHUUBNW+1Ff0NYkpi7EyyyHJR8Sc2GEikMwLZjiJ3TdnpTzUFFtg1e3jDH6zVulyraI8ar9u+PMvrYclxMCgYAcv8zd+2M4DzqZEIxfx0z5g/UWNYpE9VP/s5dFc/wm9DwT9qQoTlur848M4Dpmp+EjDFFDXTcALRbBH2NOYel4AngY6pSxmrcti6hMvAP/98ciHxIhqTFWPj9TzHrlHmjcj0B1k0v6dA7sQALN2XmmJ+gS31T4os0ajzU/SFX8gQKBgFc279aiLDmVj+WNWF2Td69trPQHwpxWc13z0b845SpWEzD70n8B7Wm4SKJ/tKTcn/XKCXb+htyvaEHt1xc6wU3R9cQTZZU88GxYLlJu4/5vPKJEvTxP9F87blpbG9KZxFIwyiEXVJSrtwFZ6EJEaXaiCev9EYOq34JBh0upYmQlAoGAPj9Cg0oTz1bkce7JUbpqSuet7Hfs9ikvaO9ZGYV9AKprL3xnwO0QLTv09X0gkQKLYluAGKElA19dV5DOl4ubwNn4A8TMtx+nyEnSLCa0Z6z22MqvIczK94pLl0we3bHB4KaJdt64iQFAoI0G4QfQ0O0utz6ztV8LK5C/IB5VXbg=-----END RSA PRIVATE KEY-----',
    '-----BEGIN RSA PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhxtApEGLPR4DN+KANEI1lnnQkDp4N4Z4tyzIPU0Uis4ztVJS5eirxvE351BslsKJ5N/DGgqQDmKh8vEZVevkYmVsXddTvy75VXo9tIIkEwihMhwj3kfcM5g8dmyruuPz8LL6FXrltaIUPdEnzwCQAOIFvMg5lpnq2ZcDGd57f2NDuD9M6LtcKWKzXB5k+IGnIaINCpaHU5gsE2NyWL4RZ6ROfjfcYGTkzkh1LQhE/6uUJjtNnekoR6LeIrQ/JiVAv8H4KvKSgeBZ/jx4wRDduFdEHNBy1IbaOUndT/BmrCCbhnFTSrQcyiS4mYRJK6CgWkUG+Dh7szQAHogImj+wcwIDAQAB-----END RSA PUBLIC KEY-----'
);