package com.barbershop.booking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}