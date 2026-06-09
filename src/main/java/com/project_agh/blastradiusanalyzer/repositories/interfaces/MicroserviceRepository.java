package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Microservice;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for managing microservice infrastructure within a
 * project, including nodes, dependencies, clusters, notes, and blast-radius
 * analysis.
 */
public interface MicroserviceRepository {
    /** Creates or updates a microservice node and links it to the project. */
    void addMicroservice(Microservice microservice, String projectId);

    /** Creates a {@code DEPENDS_ON} relationship between two nodes. */
    void createDependency(String sourceId, String targetId, String projectId);

    /** Removes a {@code DEPENDS_ON} relationship between two nodes. */
    void deleteDependency(String sourceId, String targetId, String projectId);

    /** Detaches and deletes a node (microservice or other entity) from the project. */
    void deleteNode(String id, String projectId);

    /**
     * Creates a cluster that visually groups nodes together and assigns
     * the specified nodes to that cluster.
     */
    void createCluster(String name, String color, List<String> nodeIds, String projectId);

    /** Attaches a documentation note to a service or cluster. */
    void addNote(String title, String text, String color, String targetType, String targetId, String projectId);

    /** Assigns a team as the maintainer of a microservice. */
    void assignTeamToService(String serviceId, String teamId, String projectId);

    /**
     * Performs a blast-radius analysis: returns all microservices that
     * transitively depend on the given target node.
     */
    List<Microservice> getBlastRadius(String targetId, String projectId);

    /**
     * Returns the full project topology as a map containing the list of
     * nodes and the list of edges between them.
     */
    Map<String, Object> getTopology(String projectId);
}