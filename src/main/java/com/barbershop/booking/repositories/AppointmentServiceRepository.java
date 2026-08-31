package com.barbershop.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.barbershop.booking.models.AppointmentService;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {

}
