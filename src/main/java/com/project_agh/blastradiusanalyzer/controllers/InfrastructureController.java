package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.*;
import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.services.MicroserviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for infrastructure management within a project.
 * <p>
 * Provides endpoints for managing microservices, dependencies, clusters,
 * notes, team assignments, blast-radius analysis, and topology retrieval.
 * All endpoints require a {@code Project-Id} header to scope operations
 * to the correct project.
 * </p>
 */
@RestController
@RequestMapping("/api/infra")
public class InfrastructureController {
    private final MicroserviceService service;

    /**
     * Constructs the controller with the required microservice service.
     *
     * @param service the service handling microservice business logic
     */
    public InfrastructureController(MicroserviceService service) { this.service = service; }

    /**
     * Creates a new microservice within the specified project.
     *
     * @param dto       the microservice payload (name, language)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping("/microservices")
    public ResponseEntity<String> createMicroservice(@RequestBody MicroserviceDto dto, @RequestHeader("Project-Id") String projectId) {
        Microservice ms = new Microservice(UUID.randomUUID().toString(), dto.name(), dto.language());
        service.createMicroservice(ms, projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a dependency edge from a source node to a target node.
     *
     * @param dto       the dependency payload (sourceId, targetId)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping("/dependencies")
    public ResponseEntity<?> createDependency(@RequestBody DependencyDto dto, @RequestHeader("Project-Id") String projectId) {
        service.addDependency(dto.sourceId(), dto.targetId(), projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a dependency edge between two nodes.
     *
     * @param sourceId  the source node identifier (path variable)
     * @param targetId  the target node identifier (path variable)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @DeleteMapping("/dependencies/{sourceId}/{targetId}")
    public ResponseEntity<?> deleteDependency(@PathVariable String sourceId, @PathVariable String targetId, @RequestHeader("Project-Id") String projectId) {
        service.deleteDependency(sourceId, targetId, projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a node and all its relationships from the project topology.
     *
     * @param id        the node identifier (path variable)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @DeleteMapping("/microservices/{id}")
    public ResponseEntity<?> deleteNode(@PathVariable String id, @RequestHeader("Project-Id") String projectId) {
        service.deleteNode(id, projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a visual cluster that groups multiple nodes together.
     *
     * @param dto       the cluster payload (name, color, nodeIds)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping("/clusters")
    public ResponseEntity<?> createCluster(@RequestBody ClusterDto dto, @RequestHeader("Project-Id") String projectId) {
        service.createCluster(dto.name(), dto.color(), dto.nodeIds(), projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Attaches a documentation note to a microservice or cluster.
     *
     * @param dto       the note payload (title, text, color, targetType, targetId)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping("/notes")
    public ResponseEntity<?> createNote(@RequestBody NoteDto dto, @RequestHeader("Project-Id") String projectId) {
        service.createNote(dto.title(), dto.text(), dto.color(), dto.targetType(), dto.targetId(), projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Assigns a team as the maintainer of a microservice.
     *
     * @param serviceId the microservice identifier (path variable)
     * @param teamId    the team identifier (path variable)
     * @param projectId the project identifier from the header
     * @return {@code 200 OK} on success
     */
    @PostMapping("/services/{serviceId}/assign-team/{teamId}")
    public ResponseEntity<?> assignTeam(@PathVariable String serviceId, @PathVariable String teamId, @RequestHeader("Project-Id") String projectId) {
        service.assignTeamToService(serviceId, teamId, projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Performs a blast-radius analysis for the given target node.
     * <p>
     * Returns all microservices that transitively depend on the specified
     * target, indicating which services would be affected if the target
     * were to fail.
     * </p>
     *
     * @param targetId  the node identifier to analyze (path variable)
     * @param projectId the project identifier from the header
     * @return the list of affected microservices
     */
    @GetMapping("/blast-radius/{targetId}")
    public List<Microservice> getBlastRadius(@PathVariable String targetId, @RequestHeader("Project-Id") String projectId) {
        return service.analyzeBlastRadius(targetId, projectId);
    }

    /**
     * Retrieves the complete project topology including all nodes
     * (microservices, notes, teams, workers) and the edges between them.
     *
     * @param projectId the project identifier from the header
     * @return a map containing {@code "nodes"} and {@code "edges"} lists
     */
    @GetMapping("/topology")
    public Map<String, Object> getTopology(@RequestHeader("Project-Id") String projectId) {
        return service.getTopology(projectId);
    }
}