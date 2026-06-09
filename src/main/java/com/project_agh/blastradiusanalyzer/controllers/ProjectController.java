package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.ProjectDto;
import com.project_agh.blastradiusanalyzer.services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

/**
 * REST controller for project management operations.
 * <p>
 * Provides endpoints for listing, creating, and deleting projects. All
 * operations are scoped to the currently authenticated user obtained
 * from the {@link Principal}.
 * </p>
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    /**
     * Constructs the controller with the required project service.
     *
     * @param projectService the service handling project business logic
     */
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    /**
     * Returns all projects owned by the currently authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return the list of projects for the user
     */
    @GetMapping
    public ResponseEntity<?> getProjects(Principal principal) {
        return ResponseEntity.ok(projectService.getUserProjects(principal.getName()));
    }

    /**
     * Creates a new project owned by the authenticated user.
     *
     * @param dto       the project payload containing the name
     * @param principal the authenticated user's principal
     * @return the created project
     */
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectDto dto, Principal principal) {
        return ResponseEntity.ok(projectService.createProject(dto.name(), principal.getName()));
    }

    /**
     * Deletes a project owned by the authenticated user.
     *
     * @param id        the project identifier (path variable)
     * @param principal the authenticated user's principal
     * @return {@code 200 OK} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id, Principal principal) {
        projectService.deleteProject(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}