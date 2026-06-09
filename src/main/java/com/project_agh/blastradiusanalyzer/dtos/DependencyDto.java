package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Request payload for creating or referencing a dependency between
 * two microservices in the topology graph.
 *
 * @param sourceId the identifier of the source (dependent) node
 * @param targetId the identifier of the target (dependency) node
 */
public record DependencyDto(String sourceId, String targetId) {
}
