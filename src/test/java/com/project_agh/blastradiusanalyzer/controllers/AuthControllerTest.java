package com.project_agh.blastradiusanalyzer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_agh.blastradiusanalyzer.dtos.AuthRequest;
import com.project_agh.blastradiusanalyzer.models.User;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.UserRepository;
import com.project_agh.blastradiusanalyzer.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AuthController}.
 * <p>
 * Tests the register and login endpoints using mocked repository
 * dependencies and {@link MockMvc} for HTTP request simulation.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Registers a new user successfully and receives a JWT. */
    @Test
    void registerNewUser() throws Exception {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        AuthRequest req = new AuthRequest("newuser", "pass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    /** Returns 409 Conflict when registering a duplicate username. */
    @Test
    void registerDuplicateUser() throws Exception {
        when(userRepository.findByUsername("existing")).thenReturn(
                Optional.of(new User("u1", "existing", "encoded")));

        AuthRequest req = new AuthRequest("existing", "pass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    /** Logs in with correct credentials and receives a JWT. */
    @Test
    void loginSuccess() throws Exception {
        String encoded = passwordEncoder.encode("password");
        when(userRepository.findByUsername("alice")).thenReturn(
                Optional.of(new User("u1", "alice", encoded)));

        AuthRequest req = new AuthRequest("alice", "password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    /** Returns 401 Unauthorized when the password is incorrect. */
    @Test
    void loginWrongPassword() throws Exception {
        String encoded = passwordEncoder.encode("password");
        when(userRepository.findByUsername("alice")).thenReturn(
                Optional.of(new User("u1", "alice", encoded)));

        AuthRequest req = new AuthRequest("alice", "wrongpass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    /** Returns 401 Unauthorized when the user does not exist. */
    @Test
    void loginNonExistentUser() throws Exception {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        AuthRequest req = new AuthRequest("ghost", "pass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
