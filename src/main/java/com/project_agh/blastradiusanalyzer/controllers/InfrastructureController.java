package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.*;
import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.services.MicroserviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/infra")
public class InfrastructureController {
    private final MicroserviceService service;

    public InfrastructureController(MicroserviceService service) { this.service = service; }

    @PostMapping("/microservices")
    public ResponseEntity<String> createMicroservice(@RequestBody MicroserviceDto dto, @RequestHeader("Project-Id") String projectId) {
        Microservice ms = new Microservice(UUID.randomUUID().toString(), dto.name(), dto.language());
        service.createMicroservice(ms, projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dependencies")
    public ResponseEntity<?> createDependency(@RequestBody DependencyDto dto, @RequestHeader("Project-Id") String projectId) {
        service.addDependency(dto.sourceId(), dto.targetId(), projectId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/dependencies/{sourceId}/{targetId}")
    public ResponseEntity<?> deleteDependency(@PathVariable String sourceId, @PathVariable String targetId, @RequestHeader("Project-Id") String projectId) {
        service.deleteDependency(sourceId, targetId, projectId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/microservices/{id}")
    public ResponseEntity<?> deleteNode(@PathVariable String id, @RequestHeader("Project-Id") String projectId) {
        service.deleteNode(id, projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clusters")
    public ResponseEntity<?> createCluster(@RequestBody ClusterDto dto, @RequestHeader("Project-Id") String projectId) {
        service.createCluster(dto.name(), dto.color(), dto.nodeIds(), projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notes")
    public ResponseEntity<?> createNote(@RequestBody NoteDto dto, @RequestHeader("Project-Id") String projectId) {
        service.createNote(dto.title(), dto.text(), dto.color(), dto.targetType(), dto.targetId(), projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/services/{serviceId}/assign-team/{teamId}")
    public ResponseEntity<?> assignTeam(@PathVariable String serviceId, @PathVariable String teamId, @RequestHeader("Project-Id") String projectId) {
        service.assignTeamToService(serviceId, teamId, projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blast-radius/{targetId}")
    public List<Microservice> getBlastRadius(@PathVariable String targetId, @RequestHeader("Project-Id") String projectId) {
        return service.analyzeBlastRadius(targetId, projectId);
    }

    @GetMapping("/topology")
    public Map<String, Object> getTopology(@RequestHeader("Project-Id") String projectId) {
        return service.getTopology(projectId);
    }
}