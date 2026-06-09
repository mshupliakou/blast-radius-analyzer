package com.project_agh.blastradiusanalyzer.models;

/**
 * Represents an authenticated user of the Blast Radius Analyzer.
 * <p>
 * Users are stored in the Neo4j database and are identified by their
 * username. The password field stores a BCrypt-hashed value. Each user
 * can own multiple projects.
 * </p>
 *
 * @param id       the unique identifier of the user
 * @param username the login name of the user
 * @param password the BCrypt-encoded password hash
 */
public record User(String id, String username, String password) {}