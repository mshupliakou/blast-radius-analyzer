package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.WorkerDto;
import com.project_agh.blastradiusanalyzer.services.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService service;

    public WorkerController(WorkerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createWorker(@RequestBody WorkerDto dto) {
        service.addWorker(dto.name(), dto.role(), dto.teamId());
        return ResponseEntity.ok().build();
    }
}