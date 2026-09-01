package com.barbershop.booking.dtos;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    @NotNull(message = "Employee id is required")
    private Long employeeId;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Date and time are required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime dateTime;

    @NotEmpty(message = "At least one service must be selected")
    private List<Long> serviceIds;
}
