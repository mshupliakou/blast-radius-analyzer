package com.project_agh.blastradiusanalyzer.repositories.interfaces;

public interface WorkerRepository {
    void addWorker(String name, String role, String teamId, String projectId);
}