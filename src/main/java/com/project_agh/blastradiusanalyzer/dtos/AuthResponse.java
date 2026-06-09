package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Response payload returned after successful authentication.
 *
 * @param token    the JWT bearer token for subsequent API requests
 * @param username the username of the authenticated user
 */
public record AuthResponse(String token, String username) {}