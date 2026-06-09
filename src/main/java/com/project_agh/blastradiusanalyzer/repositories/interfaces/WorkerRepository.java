package com.project_agh.blastradiusanalyzer.repositories.interfaces;

/**
 * Repository interface for managing workers within a project and team
 * context.
 */
public interface WorkerRepository {
    /** Creates a new worker node, associates it with a team, and links it to the project. */
    void addWorker(String name, String role, String teamId, String projectId);
}