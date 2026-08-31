package com.barbershop.booking.repositories;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.Employee;
import com.barbershop.booking.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEmployeeAndDayOfWeek(Employee employee, DayOfWeek day);
}
