package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Microservice;

import java.util.List;
import java.util.Map;

public interface MicroserviceRepository {
    void addMicroservice(Microservice microservice);
    void createDependency(String sourceId, String targetId);
    List<Microservice> getBlastRadius(String targetId);
    Map<String, Object> getTopology();
    void deleteMicroservice(String id);
    void deleteDependency(String sourceId, String targetId);
    void createCluster(String name, String color, List<String> nodeIds);
    void deleteNode(String id);
    void addNote(String title, String text, String color, String targetType, String targetId);
}