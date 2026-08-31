package com.barbershop.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

}