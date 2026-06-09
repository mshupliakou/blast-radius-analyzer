package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.repositories.interfaces.WorkerRepository;
import org.springframework.stereotype.Service;

/**
 * Service layer for worker management operations.
 * <p>
 * Delegates all data access to {@link WorkerRepository} and provides
 * methods for adding workers to teams within a project.
 * </p>
 */
@Service
public class WorkerService {
    private final WorkerRepository repository;

    /**
     * Constructs the service with the required repository dependency.
     *
     * @param repository the worker repository to delegate to
     */
    public WorkerService(WorkerRepository repository) {
        this.repository = repository;
    }

    /** Adds a new worker to a team within the specified project. */
    public void addWorker(String name, String role, String teamId, String projectId) {
        repository.addWorker(name, role, teamId, projectId);
    }
}