package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Project;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository repository;
    public ProjectService(ProjectRepository repository) { this.repository = repository; }
    public Project createProject(String name, String username) { return repository.createProject(name, username); }
    public List<Project> getUserProjects(String username) { return repository.getUserProjects(username); }
    public void deleteProject(String projectId, String username) { repository.deleteProject(projectId, username); }
}