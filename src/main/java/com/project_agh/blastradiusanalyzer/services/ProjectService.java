package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Project;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer for project management operations.
 * <p>
 * Delegates all data access to {@link ProjectRepository} and provides
 * methods for creating, listing, and deleting projects on behalf of a
 * specific user.
 * </p>
 */
@Service
public class ProjectService {
    private final ProjectRepository repository;

    /**
     * Constructs the service with the required repository dependency.
     *
     * @param repository the project repository to delegate to
     */
    public ProjectService(ProjectRepository repository) { this.repository = repository; }

    /** Creates a new project owned by the specified user. */
    public Project createProject(String name, String username) { return repository.createProject(name, username); }

    /** Returns all projects owned by the given user. */
    public List<Project> getUserProjects(String username) { return repository.getUserProjects(username); }

    /** Deletes a project if the requesting user is its owner. */
    public void deleteProject(String projectId, String username) { repository.deleteProject(projectId, username); }
}