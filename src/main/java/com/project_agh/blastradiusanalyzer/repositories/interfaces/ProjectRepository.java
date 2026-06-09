package com.project_agh.blastradiusanalyzer.repositories.interfaces;
import com.project_agh.blastradiusanalyzer.models.Project;
import java.util.List;

public interface ProjectRepository {
    Project createProject(String name, String username);
    List<Project> getUserProjects(String username);
    void deleteProject(String projectId, String username);
}