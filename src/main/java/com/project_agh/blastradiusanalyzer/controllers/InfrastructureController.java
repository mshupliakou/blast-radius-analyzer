package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.ClusterDto;
import com.project_agh.blastradiusanalyzer.dtos.DependencyDto;
import com.project_agh.blastradiusanalyzer.dtos.MicroserviceDto;
import com.project_agh.blastradiusanalyzer.dtos.NoteDto;
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

    public InfrastructureController(MicroserviceService service) {
        this.service = service;
    }

    @PostMapping("/microservices")
    public ResponseEntity<String> createMicroservice(@RequestBody MicroserviceDto dto) {
        String newId = UUID.randomUUID().toString();
        Microservice ms = new Microservice(newId, dto.name(), dto.language());
        service.createMicroservice(ms);
        return ResponseEntity.ok("Created with ID: " + newId);
    }

    @PostMapping("/dependencies")
    public ResponseEntity<?> createDependency(@RequestBody DependencyDto dto) {
        service.addDependency(dto.sourceId(), dto.targetId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blast-radius/{targetId}")
    public List<Microservice> getBlastRadius(@PathVariable String targetId) {
        return service.analyzeBlastRadius(targetId);
    }

    @GetMapping("/topology")
    public Map<String, Object> getTopology() {
        return service.getTopology();
    }

    @DeleteMapping("/microservices/{id}")
    public ResponseEntity<?> deleteMicroservice(@PathVariable String id) {
        service.deleteNode(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/dependencies/{sourceId}/{targetId}")
    public ResponseEntity<?> deleteDependency(@PathVariable String sourceId, @PathVariable String targetId) {
        service.deleteDependency(sourceId, targetId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clusters")
    public ResponseEntity<?> createCluster(@RequestBody ClusterDto dto) {
        service.createCluster(dto.name(), dto.color(), dto.nodeIds());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notes")
    public ResponseEntity<?> createNote(@RequestBody NoteDto dto) {
        service.createNote(dto.title(), dto.text(), dto.color(), dto.targetType(), dto.targetId());
        return ResponseEntity.ok().build();
    }
}