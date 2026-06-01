package com.project_agh.blastradiusanalyzer.services;


import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.MicroserviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MicroserviceService {
    private final MicroserviceRepository repository;

    public MicroserviceService(MicroserviceRepository repository) {
        this.repository = repository;
    }

    public void createMicroservice(Microservice microservice) {
        repository.addMicroservice(microservice);
    }

    public void addDependency(String sourceId, String targetId) {
        repository.createDependency(sourceId, targetId);
    }

    public List<Microservice> analyzeBlastRadius(String targetId) {
        return repository.getBlastRadius(targetId);
    }

    public Map<String, Object> getTopology() {
        return repository.getTopology();
    }
}