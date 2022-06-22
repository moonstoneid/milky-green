package org.mn.web3login.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "oauth2_authorization")
public class Authorization {

    @Id
    @Column(name = "id", length = 100)
    private String id;
    @Column(name = "registered_client_id", length = 100)
    private String registeredClientId;
    @Column(name = "principal_name", length = 200)
    private String principalName;
    @Column(name = "authorization_grant_type", length = 100)
    private String authorizationGrantType;

    @Lob
    @Column(name = "attributes")
    private String attributes;

    @Column(name = "state", length = 500)
    private String state;

    @Column(name = "authorization_code_value", length = 4000)
    private String authorizationCodeValue;
    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;
    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;
    @Lob
    @Column(name = "authorization_code_metadata")
    private String authorizationCodeMetadata;

    @Column(name = "access_token_value", length = 4000)
    private String accessTokenValue;
    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;
    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;
    @Column(name = "access_token_scopes", length = 1000)
    private String accessTokenScopes;
    @Lob
    @Column(name = "access_token_metadata")
    private String accessTokenMetadata;

    @Column(name = "refresh_token_value", length = 4000)
    private String refreshTokenValue;
    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;
    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;
    @Lob
    @Column(name = "refresh_token_metadata")
    private String refreshTokenMetadata;

    @Column(name = "id_token_value", length = 4000)
    private String idTokenValue;
    @Column(name = "id_token_issued_at")
    private Instant idTokenIssuedAt;
    @Column(name = "id_token_expires_at")
    private Instant idTokenExpiresAt;
    @Column(name = "id_token_claims", length = 2000)
    private String idTokenClaims;
    @Lob
    @Column(name = "id_token_metadata")
    private String idTokenMetadata;

}
