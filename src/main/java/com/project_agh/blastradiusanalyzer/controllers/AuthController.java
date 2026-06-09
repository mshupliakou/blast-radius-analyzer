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

/**
 * REST controller for authentication operations.
 * <p>
 * Provides public endpoints for user registration and login. On
 * successful authentication, a signed JWT is returned which must be
 * included in the {@code Authorization} header of subsequent requests.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructs the controller with the required dependencies.
     *
     * @param userRepository   repository for user persistence
     * @param passwordEncoder  encoder for hashing and verifying passwords
     * @param jwtService       service for generating JWT tokens
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user account.
     * <p>
     * If the username already exists, a {@code 409 Conflict} response is
     * returned. Otherwise the user is persisted with a BCrypt-hashed
     * password and a JWT is issued immediately.
     * </p>
     *
     * @param request the registration payload containing username and password
     * @return {@code 200 OK} with a JWT and username, or {@code 409 Conflict}
     */
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

    /**
     * Authenticates an existing user.
     * <p>
     * Validates the username and password combination. Returns a signed
     * JWT on success, or {@code 401 Unauthorized} if the credentials are
     * invalid.
     * </p>
     *
     * @param request the login payload containing username and password
     * @return {@code 200 OK} with a JWT and username, or {@code 401 Unauthorized}
     */
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