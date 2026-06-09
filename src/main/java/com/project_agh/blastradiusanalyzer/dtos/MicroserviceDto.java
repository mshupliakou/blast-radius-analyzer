package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Request payload for creating a new microservice within a project.
 *
 * @param name     the display name of the microservice
 * @param language the programming language used by the microservice
 */
public record MicroserviceDto(String name, String language) {
}