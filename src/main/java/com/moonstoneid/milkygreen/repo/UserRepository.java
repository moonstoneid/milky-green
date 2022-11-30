package com.moonstoneid.milkygreen.repo;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.milkygreen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query("select u from User u")
    List<User> getAll();

}
