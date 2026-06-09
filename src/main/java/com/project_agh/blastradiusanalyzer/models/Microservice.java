package com.project_agh.blastradiusanalyzer.models;

/**
 * Represents a microservice node within a project topology.
 * <p>
 * Each microservice has a unique identifier, a human-readable name,
 * and a programming language that it is implemented in. Microservices
 * are stored as nodes in the Neo4j graph and can be connected via
 * {@code DEPENDS_ON} relationships to form dependency chains.
 * </p>
 *
 * @param id       the unique identifier of the microservice
 * @param name     the display name of the microservice
 * @param language the programming language (e.g., "Java", "Python")
 */
public record Microservice(String id, String name, String language) {
}