package com.moonstoneid.web3login.repo;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3login.model.AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationConsentRepository extends JpaRepository<AuthorizationConsent,
        AuthorizationConsent.AuthorizationConsentId> {

    Optional<AuthorizationConsent> findByClientIdAndUsername(String clientId, String username);

    @Query("select a from AuthorizationConsent a" +
            " where a.username = :username"
    )
    List<AuthorizationConsent> getAuthorizationConsentsByUsername(
            @Param("username") String username);

}
