package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.repositories.interfaces.WorkerRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {
    private final WorkerRepository repository;

    public WorkerService(WorkerRepository repository) {
        this.repository = repository;
    }

    public void addWorker(String name, String role, String teamId) {
        repository.addWorker(name, role, teamId);
    }
}