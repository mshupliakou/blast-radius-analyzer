package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.AuthRequest;
import com.project_agh.blastradiusanalyzer.dtos.AuthResponse;
import com.project_agh.blastradiusanalyzer.models.User;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.UserRepository;
import com.project_agh.blastradiusanalyzer.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User newUser = new User(
                UUID.randomUUID().toString(),
                request.username(),
                passwordEncoder.encode(request.password())
        );
        userRepository.saveUser(newUser);

        String token = jwtService.generateToken(newUser.username());
        return ResponseEntity.ok(new AuthResponse(token, newUser.username()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isPresent() && passwordEncoder.matches(request.password(), userOpt.get().password())) {
            String token = jwtService.generateToken(userOpt.get().username());
            return ResponseEntity.ok(new AuthResponse(token, userOpt.get().username()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}