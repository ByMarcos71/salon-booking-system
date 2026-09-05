package com.barbershop.booking.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 5, max = 15)
    private String username;
    @NotBlank(message = "password is required")
    @Size(min = 6)
    private String password;
    @NotBlank(message = "email is required")
    @Email(message = "Invalid Email address")
    private String email;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[679]\\d{8}$", message = "Phone number must be a valid Spanish number (9 digits)")
    private String phoneNumber;
}
