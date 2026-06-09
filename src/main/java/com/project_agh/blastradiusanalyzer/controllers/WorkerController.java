package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.WorkerDto;
import com.project_agh.blastradiusanalyzer.services.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for worker management within a project.
 * <p>
 * Provides an endpoint for adding workers to a team. The
 * {@code Project-Id} header and the team identifier in the request body
 * scope the operation to the correct project and team.
 * </p>
 */
@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService service;

    /**
     * Constructs the controller with the required worker service.
     *
     * @param service the service handling worker business logic
     */
    public WorkerController(WorkerService service) {
        this.service = service;
    }

    /**
     * Adds a new worker to a team within the specified project.
     *
     * @param dto       the worker payload (name, role, teamId)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping
    public ResponseEntity<?> createWorker(@RequestBody WorkerDto dto, @RequestHeader("Project-Id") String projectId) {
        service.addWorker(dto.name(), dto.role(), dto.teamId(), projectId);
        return ResponseEntity.ok().build();
    }
}