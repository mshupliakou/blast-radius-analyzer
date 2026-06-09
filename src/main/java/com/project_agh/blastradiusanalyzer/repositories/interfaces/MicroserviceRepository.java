package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Microservice;

import java.util.List;
import java.util.Map;

public interface MicroserviceRepository {
    void addMicroservice(Microservice microservice, String projectId);
    void createDependency(String sourceId, String targetId, String projectId);
    void deleteDependency(String sourceId, String targetId, String projectId);
    void deleteNode(String id, String projectId);
    void createCluster(String name, String color, List<String> nodeIds, String projectId);
    void addNote(String title, String text, String color, String targetType, String targetId, String projectId);
    void assignTeamToService(String serviceId, String teamId, String projectId);
    List<Microservice> getBlastRadius(String targetId, String projectId);
    Map<String, Object> getTopology(String projectId);
}