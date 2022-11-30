package com.moonstoneid.milkygreen.repo;

import com.moonstoneid.milkygreen.model.KeyPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyPairRepository extends JpaRepository<KeyPair, String> {

    Optional<KeyPair> findById(String keyPairId);

}
