package com.barbershop.booking.dtos;

import java.time.LocalDateTime;
import java.util.List;
import com.barbershop.booking.models.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String employeeName;
    private String userName;
    private LocalDateTime dateTime;
    private List<String> serviceNames;
    private AppointmentStatus status;
}
