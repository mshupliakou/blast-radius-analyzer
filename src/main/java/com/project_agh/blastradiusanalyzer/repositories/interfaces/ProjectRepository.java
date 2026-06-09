package com.project_agh.blastradiusanalyzer.repositories.interfaces;
import com.project_agh.blastradiusanalyzer.models.Project;
import java.util.List;

/**
 * Repository interface for managing projects and their ownership in the
 * Neo4j database.
 */
public interface ProjectRepository {
    /** Creates a new project owned by the specified user. */
    Project createProject(String name, String username);

    /** Returns all projects owned by the given user. */
    List<Project> getUserProjects(String username);

    /** Deletes a project if the requesting user is its owner. */
    void deleteProject(String projectId, String username);
}