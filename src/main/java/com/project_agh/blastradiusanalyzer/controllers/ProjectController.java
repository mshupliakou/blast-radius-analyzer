package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.ProjectDto;
import com.project_agh.blastradiusanalyzer.services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @GetMapping
    public ResponseEntity<?> getProjects(Principal principal) {
        return ResponseEntity.ok(projectService.getUserProjects(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectDto dto, Principal principal) {
        return ResponseEntity.ok(projectService.createProject(dto.name(), principal.getName()));
    }
}