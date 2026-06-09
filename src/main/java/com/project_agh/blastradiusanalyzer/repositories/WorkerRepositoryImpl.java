package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.repositories.interfaces.WorkerRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

/**
 * Neo4j-backed implementation of {@link WorkerRepository}.
 * <p>
 * Workers are stored as nodes with a {@code WORKS_IN} relationship to
 * their team and an {@code IN_PROJECT} relationship to their project.
 * </p>
 */
@Repository
class WorkerRepositoryImpl implements WorkerRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    /**
     * Constructs the repository with the Neo4j driver and session configuration.
     *
     * @param driver        the Neo4j driver instance
     * @param sessionConfig the session configuration targeting the correct database
     */
    public WorkerRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void addWorker(String name, String role, String teamId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            // Match team by custom UUID or built-in Neo4j ID
            String cypher = """
                MATCH (t:Team)
                WHERE toString(t.id) = $teamId OR toString(id(t)) = $teamId
                MATCH (p:Project {id: $projectId})
                CREATE (w:Worker {id: randomUUID(), name: $name, role: $role})
                CREATE (w)-[:WORKS_IN]->(t)
                CREATE (w)-[:IN_PROJECT]->(p)
                """;
            session.run(cypher, Values.parameters("name", name, "role", role, "teamId", teamId, "projectId", projectId));
        }
    }
}