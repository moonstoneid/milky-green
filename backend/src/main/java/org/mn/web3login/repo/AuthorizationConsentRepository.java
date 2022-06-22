package org.mn.web3login.repo;

import java.util.Optional;

import org.mn.web3login.model.AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationConsentRepository extends JpaRepository<AuthorizationConsent,
        AuthorizationConsent.AuthorizationConsentId> {

    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
            String principalName);

    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

}
