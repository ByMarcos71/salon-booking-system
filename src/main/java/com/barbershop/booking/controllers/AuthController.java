package com.barbershop.booking.controllers;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbershop.booking.dtos.LoginRequest;
import com.barbershop.booking.dtos.RegisterRequest;
import com.barbershop.booking.dtos.RegisterResponse;
import com.barbershop.booking.models.User;
import com.barbershop.booking.models.enums.Role;
import com.barbershop.booking.repositories.UserRepository;
import com.barbershop.booking.services.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        // 1. Busca el User por username (Optional, ya sabes cómo)
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        // 2. Si el Optional está vacío -> devuelve 401 con el mensaje genérico
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(token);

    }

}
