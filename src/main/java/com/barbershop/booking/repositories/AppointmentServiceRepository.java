package com.barbershop.booking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.Appointment;
import com.barbershop.booking.models.AppointmentService;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    List<AppointmentService> findByAppointment(Appointment appointment);
}
