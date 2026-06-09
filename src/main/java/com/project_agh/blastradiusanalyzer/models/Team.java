package com.project_agh.blastradiusanalyzer.models;

/**
 * Represents a team of workers that can be assigned to maintain one or
 * more microservices within a project.
 * <p>
 * Teams are stored as nodes in the Neo4j graph. Workers are linked to
 * their team via a {@code WORKS_IN} relationship, and microservices are
 * linked to their responsible team via a {@code MAINTAINED_BY}
 * relationship.
 * </p>
 *
 * @param id   the unique identifier of the team
 * @param name the display name of the team
 */
public record Team(String id, String name) {
}
