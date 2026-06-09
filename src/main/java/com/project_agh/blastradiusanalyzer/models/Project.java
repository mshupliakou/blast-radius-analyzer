package com.project_agh.blastradiusanalyzer.models;

/**
 * Represents a top-level project that contains microservices, teams,
 * clusters, and notes within the Blast Radius Analyzer.
 * <p>
 * Each project is owned by a single user and serves as an isolation
 * boundary: graph entities belonging to one project are invisible to
 * other projects.
 * </p>
 *
 * @param id          the unique identifier of the project
 * @param name        the display name of the project
 * @param description a short description of the project (may be empty)
 * @param ownerId     the username of the project owner
 */
public record Project(String id, String name, String description, String ownerId) {}