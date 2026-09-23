package com.barbershop.booking.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.barbershop.booking.dtos.AppointmentResponse;
import com.barbershop.booking.dtos.CreateAppointmentRequest;
import com.barbershop.booking.models.Appointment;
import com.barbershop.booking.models.AppointmentService;
import com.barbershop.booking.models.Employee;
import com.barbershop.booking.models.Service;
import com.barbershop.booking.models.User;
import com.barbershop.booking.models.enums.AppointmentStatus;
import com.barbershop.booking.repositories.AppointmentRepository;
import com.barbershop.booking.repositories.AppointmentServiceRepository;
import com.barbershop.booking.repositories.EmployeeRepository;
import com.barbershop.booking.repositories.ServiceRepository;
import com.barbershop.booking.repositories.UserRepository;
import com.barbershop.booking.services.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final BookingService bookingService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentController(BookingService bookingService,
            AppointmentRepository appointmentRepository,
            AppointmentServiceRepository appointmentServiceRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository) {
        this.bookingService = bookingService;
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Service> services = serviceRepository.findAllById(request.getServiceIds());

        int duration = 0;
        for (Service service : services) {
            duration += service.getDuration();
        }

        if (!(bookingService.isAvailable(employee, request.getDateTime(), duration))) {
            return ResponseEntity.status(409).body("slot not avaliable");
        }

        Appointment appointment = new Appointment();
        appointment.setDateTime(request.getDateTime());
        appointment.setEmployee(employee);
        appointment.setUser(user);
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        for (Service service : services) {
            AppointmentService appointmentService = new AppointmentService();
            appointmentService.setAppointment(savedAppointment);
            appointmentService.setService(service);
            appointmentServiceRepository.save(appointmentService);
        }

        AppointmentResponse response = new AppointmentResponse();
        response.setId(savedAppointment.getId());
        response.setEmployeeName(employee.getName());
        response.setUserName(user.getUsername());
        response.setDateTime(savedAppointment.getDateTime());
        response.setStatus(savedAppointment.getStatus());
        response.setServiceNames(services.stream().map(Service::getName).toList());

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public List<AppointmentResponse> getMyAppointments(Authentication authentication) {
        String username = authentication.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        User actUser = userOpt.orElseThrow(() -> new RuntimeException("user not found"));

        List<Appointment> userAppointments = appointmentRepository.findByUser(actUser);

        List<AppointmentResponse> responses = userAppointments.stream().map(appointment -> {

            List<AppointmentService> appointmentServices = appointmentServiceRepository.findByAppointment(appointment);

            List<String> serviceNames = appointmentServices.stream()
                    .map(as -> as.getService().getName())
                    .toList();

            AppointmentResponse response = new AppointmentResponse();
            response.setId(appointment.getId());
            response.setEmployeeName(appointment.getEmployee().getName());
            response.setUserName(appointment.getUser().getUsername());
            response.setDateTime(appointment.getDateTime());
            response.setServiceNames(serviceNames);
            response.setStatus(appointment.getStatus());

            return response;

        }).toList();

        return responses;

    }

}
