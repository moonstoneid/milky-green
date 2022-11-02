CREATE TABLE settings (
  setting_name VARCHAR(50) NOT NULL,
  setting_value VARCHAR(50) NOT NULL,
  PRIMARY KEY (setting_name)
);

INSERT INTO settings (setting_name, setting_value)
  VALUES ('allow_auto_import', 'true');

CREATE TABLE jwk_keypair (
  id VARCHAR(100) NOT NULL,
  private_key VARCHAR(2000) DEFAULT NULL,
  public_key VARCHAR(2000) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user (
  username VARCHAR(50) NOT NULL,
  enabled INT NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE user_ens (
  username VARCHAR(50) NOT NULL,
  ens_domain VARCHAR(255) DEFAULT NULL,
  ens_email VARCHAR(255) DEFAULT NULL,
  ens_url VARCHAR(1000) DEFAULT NULL,
  ens_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (username),
  CONSTRAINT fk_userens_user FOREIGN KEY (username)
    REFERENCES user (username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE oauth2_client (
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
  client_id VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL,
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
  PRIMARY KEY (id),
  KEY fk_oauth2authorization_clientid (client_id),
  KEY fk_oauth2authorization_username (username),
  CONSTRAINT fk_oauth2authorization_oauth2client FOREIGN KEY (client_id)
    REFERENCES oauth2_client (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_oauth2authorization_user FOREIGN KEY (username)
    REFERENCES user (username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE oauth2_authorization_consent (
  client_id VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL,
  authorities VARCHAR(1000) NOT NULL,
  PRIMARY KEY (client_id, username),
  KEY fk_oauth2authorizationconsent_clientid (client_id),
  KEY fk_oauth2authorizationconsent_username (username),
  CONSTRAINT fk_oauth2authorizationconsent_oauth2client FOREIGN KEY (client_id)
    REFERENCES oauth2_client (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_oauth2authorizationconsent_user FOREIGN KEY (username)
    REFERENCES user (username) ON DELETE CASCADE ON UPDATE CASCADE
);