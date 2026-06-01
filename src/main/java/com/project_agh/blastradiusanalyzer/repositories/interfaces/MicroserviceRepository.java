package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Microservice;

import java.util.List;
import java.util.Map;

public interface MicroserviceRepository {
    void addMicroservice(Microservice microservice);
    void createDependency(String sourceId, String targetId);
    List<Microservice> getBlastRadius(String targetId);
    Map<String, Object> getTopology();
}