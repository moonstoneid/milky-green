package com.moonstoneid.milkygreen.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(AuthorizationConsent.AuthorizationConsentId.class)
public class AuthorizationConsent {

    @Id
    @Column(name = "client_id", length = 100)
    private String clientId;
    @Id
    @Column(name = "username", length = 200)
    private String username;
    @Column(name = "authorities", length = 1000)
    private String authorities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationConsentId implements Serializable {

        private String clientId;
        private String username;

    }

}
