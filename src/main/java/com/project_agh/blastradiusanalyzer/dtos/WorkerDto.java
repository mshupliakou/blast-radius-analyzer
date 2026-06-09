package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Request payload for adding a worker to a team within a project.
 *
 * @param name   the display name of the worker
 * @param role   the role or job title of the worker (e.g., "Developer")
 * @param teamId the identifier of the team the worker belongs to
 */
public record WorkerDto(String name, String role, String teamId) {
}