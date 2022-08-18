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

    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
            String principalName);

    @Query("select a from AuthorizationConsent a" +
            " where a.principalName = :principalName"
    )
    List<AuthorizationConsent> getAuthorizationConsentsByPrincipalName(
            @Param("principalName") String principalName);

}
