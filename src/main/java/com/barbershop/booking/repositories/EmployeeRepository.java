package com.barbershop.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.booking.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
