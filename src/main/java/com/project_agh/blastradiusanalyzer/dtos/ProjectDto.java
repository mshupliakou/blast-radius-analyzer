package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Data transfer object representing a project, typically used in API
 * request and response payloads.
 *
 * @param id   the unique identifier of the project (may be null for creation requests)
 * @param name the display name of the project
 */
public record ProjectDto(String id, String name) {}