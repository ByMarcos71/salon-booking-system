package com.barbershop.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {

}
