package com.movieticket.repository;

import com.movieticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // For duplicate check
    boolean existsByEmail(String email);

    // For your current login flow (plain-text)
    Optional<User> findByEmailAndPassword(String email, String password);

    // (Optional, handy later if you switch to hashing:)
    Optional<User> findByEmail(String email);
}
