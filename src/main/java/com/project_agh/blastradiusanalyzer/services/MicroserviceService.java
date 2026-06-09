package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.MicroserviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service layer for microservice infrastructure operations.
 * <p>
 * Delegates all data access to {@link MicroserviceRepository} and serves
 * as the transactional boundary for operations such as creating
 * microservices, managing dependencies, organizing clusters, attaching
 * notes, assigning teams, and performing blast-radius analysis.
 * </p>
 */
@Service
public class MicroserviceService {
    private final MicroserviceRepository repository;

    /**
     * Constructs the service with the required repository dependency.
     *
     * @param repository the microservice repository to delegate to
     */
    public MicroserviceService(MicroserviceRepository repository) {
        this.repository = repository;
    }

    /** Creates a new microservice within the specified project. */
    public void createMicroservice(Microservice microservice, String projectId) { repository.addMicroservice(microservice, projectId); }

    /** Adds a dependency edge between two nodes in the project topology. */
    public void addDependency(String sourceId, String targetId, String projectId) { repository.createDependency(sourceId, targetId, projectId); }

    /** Removes a dependency edge between two nodes. */
    public void deleteDependency(String sourceId, String targetId, String projectId) { repository.deleteDependency(sourceId, targetId, projectId); }

    /** Deletes a node from the project topology. */
    public void deleteNode(String id, String projectId) { repository.deleteNode(id, projectId); }

    /** Creates a visual cluster grouping multiple nodes together. */
    public void createCluster(String name, String color, List<String> nodeIds, String projectId) { repository.createCluster(name, color, nodeIds, projectId); }

    /** Attaches a documentation note to a service or cluster. */
    public void createNote(String title, String text, String color, String targetType, String targetId, String projectId) { repository.addNote(title, text, color, targetType, targetId, projectId); }

    /** Assigns a team as the maintainer of a microservice. */
    public void assignTeamToService(String serviceId, String teamId, String projectId) { repository.assignTeamToService(serviceId, teamId, projectId); }

    /** Analyzes the blast radius of a target by finding all upstream dependents. */
    public List<Microservice> analyzeBlastRadius(String targetId, String projectId) { return repository.getBlastRadius(targetId, projectId); }

    /** Returns the full topology (nodes and edges) for the project. */
    public Map<String, Object> getTopology(String projectId) { return repository.getTopology(projectId); }
}