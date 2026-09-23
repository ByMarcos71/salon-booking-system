package com.barbershop.booking.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.Appointment;
import com.barbershop.booking.models.Employee;
import com.barbershop.booking.models.User;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByEmployeeAndDateTimeBetween(Employee employee, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByUser(User user);
}
