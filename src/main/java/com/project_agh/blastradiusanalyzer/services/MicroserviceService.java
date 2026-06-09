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

    public void createMicroservice(Microservice microservice, String projectId) { repository.addMicroservice(microservice, projectId); }
    public void addDependency(String sourceId, String targetId, String projectId) { repository.createDependency(sourceId, targetId, projectId); }
    public void deleteDependency(String sourceId, String targetId, String projectId) { repository.deleteDependency(sourceId, targetId, projectId); }
    public void deleteNode(String id, String projectId) { repository.deleteNode(id, projectId); }
    public void createCluster(String name, String color, List<String> nodeIds, String projectId) { repository.createCluster(name, color, nodeIds, projectId); }
    public void createNote(String title, String text, String color, String targetType, String targetId, String projectId) { repository.addNote(title, text, color, targetType, targetId, projectId); }
    public void assignTeamToService(String serviceId, String teamId, String projectId) { repository.assignTeamToService(serviceId, teamId, projectId); }
    public List<Microservice> analyzeBlastRadius(String targetId, String projectId) { return repository.getBlastRadius(targetId, projectId); }
    public Map<String, Object> getTopology(String projectId) { return repository.getTopology(projectId); }
}