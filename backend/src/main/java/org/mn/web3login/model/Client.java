package org.mn.web3login.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "oauth2_registered_client")
public class Client {

    @Id
    @Column(name = "id", length = 100)
    private String id;
    @Column(name = "client_id", length = 100)
    private String clientId;
    @Column(name = "client_id_issued_at")
    private Instant clientIdIssuedAt;
    @Column(name = "client_secret", length = 200)
    private String clientSecret;
    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;
    @Column(name = "client_name", length = 200)
    private String clientName;
    @Column(name = "client_authentication_methods", length = 1000)
    private String clientAuthenticationMethods;
    @Column(name = "authorization_grant_types", length = 1000)
    private String authorizationGrantTypes;
    @Column(name = "redirect_uris", length = 1000)
    private String redirectUris;
    @Column(name = "scopes", length = 1000)
    private String scopes;
    @Column(name = "client_settings", length = 2000)
    private String clientSettings;
    @Column(name = "token_settings", length = 2000)
    private String tokenSettings;

}
