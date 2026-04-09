package com.legal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.legal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 🔥 MUST
}