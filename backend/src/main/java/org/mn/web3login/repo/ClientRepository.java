package org.mn.web3login.repo;

import java.util.List;
import java.util.Optional;

import org.mn.web3login.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByClientId(String clientId);

    @Query("select c from Client c")
    List<Client> getAll();

}
