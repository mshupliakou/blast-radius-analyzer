package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Request payload for user registration and login endpoints.
 *
 * @param username the username for authentication
 * @param password the plain-text password for authentication
 */
public record AuthRequest(String username, String password) {}
