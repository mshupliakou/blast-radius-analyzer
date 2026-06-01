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

    public void createMicroservice(Microservice microservice) { repository.addMicroservice(microservice); }
    public void addDependency(String sourceId, String targetId) { repository.createDependency(sourceId, targetId); }
    public void deleteDependency(String sourceId, String targetId) { repository.deleteDependency(sourceId, targetId); }
    public void deleteNode(String id) { repository.deleteNode(id); }
    public void createCluster(String name, String color, List<String> nodeIds) { repository.createCluster(name, color, nodeIds); }
    public void createNote(String title, String text, String color, String targetType, String targetId) { repository.addNote(title, text, color, targetType, targetId); }
    public void assignTeamToService(String serviceId, String teamId) { repository.assignTeamToService(serviceId, teamId); }
    public List<Microservice> analyzeBlastRadius(String targetId) { return repository.getBlastRadius(targetId); }
    public Map<String, Object> getTopology() { return repository.getTopology(); }
}