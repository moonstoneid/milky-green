CREATE TABLE users (
  username VARCHAR(50) NOT NULL,
  enabled INT NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE authorities (
 username VARCHAR(50) NOT NULL,
 authority VARCHAR(100) NOT NULL,
 PRIMARY KEY (username)
);

CREATE TABLE oauth2_registered_client (
  id VARCHAR(100) NOT NULL,
  client_id VARCHAR(100) NOT NULL,
  client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  client_secret VARCHAR(200) DEFAULT NULL,
  client_secret_expires_at TIMESTAMP DEFAULT NULL,
  client_name VARCHAR(200) NOT NULL,
  client_authentication_methods VARCHAR(1000) NOT NULL,
  authorization_grant_types VARCHAR(1000) NOT NULL,
  redirect_uris VARCHAR(1000) DEFAULT NULL,
  scopes VARCHAR(1000) NOT NULL,
  client_settings VARCHAR(2000) NOT NULL,
  token_settings VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
  id VARCHAR(100) NOT NULL,
  registered_client_id VARCHAR(100) NOT NULL,
  principal_name VARCHAR(200) NOT NULL,
  authorization_grant_type VARCHAR(100) NOT NULL,
  attributes BLOB DEFAULT NULL,
  state VARCHAR(500) DEFAULT NULL,
  authorization_code_value BLOB DEFAULT NULL,
  authorization_code_issued_at TIMESTAMP DEFAULT NULL,
  authorization_code_expires_at TIMESTAMP DEFAULT NULL,
  authorization_code_metadata BLOB DEFAULT NULL,
  access_token_value BLOB DEFAULT NULL,
  access_token_issued_at TIMESTAMP DEFAULT NULL,
  access_token_expires_at TIMESTAMP DEFAULT NULL,
  access_token_scopes VARCHAR(1000) DEFAULT NULL,
  access_token_metadata BLOB DEFAULT NULL,
  refresh_token_value BLOB DEFAULT NULL,
  refresh_token_issued_at TIMESTAMP DEFAULT NULL,
  refresh_token_expires_at TIMESTAMP DEFAULT NULL,
  refresh_token_metadata BLOB DEFAULT NULL,
  id_token_value BLOB DEFAULT NULL,
  id_token_issued_at TIMESTAMP DEFAULT NULL,
  id_token_expires_at TIMESTAMP DEFAULT NULL,
  id_token_claims VARCHAR(2000) DEFAULT NULL,
  id_token_metadata BLOB DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization_consent (
  registered_client_id VARCHAR(100) NOT NULL,
  principal_name VARCHAR(200) NOT NULL,
  authorities VARCHAR(1000) NOT NULL,
  PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE jwk_keypair (
  id VARCHAR(100) NOT NULL,
  private_key VARCHAR(2000) DEFAULT NULL,
  public_key VARCHAR(2000) DEFAULT NULL,
  PRIMARY KEY (id)
);