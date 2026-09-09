package com.barbershop.booking.controllers;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbershop.booking.dtos.RegisterRequest;
import com.barbershop.booking.dtos.RegisterResponse;
import com.barbershop.booking.models.User;
import com.barbershop.booking.models.enums.Role;
import com.barbershop.booking.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).body("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        User registeredUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setId(registeredUser.getId());
        response.setUsername(registeredUser.getUsername());
        response.setEmail(registeredUser.getEmail());
        response.setPhoneNumber(registeredUser.getPhoneNumber());

        return ResponseEntity.status(201).body(response);

    }
}
