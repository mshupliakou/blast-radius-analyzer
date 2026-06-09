package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.repositories.interfaces.WorkerRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

@Repository
class WorkerRepositoryImpl implements WorkerRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public WorkerRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void addWorker(String name, String role, String teamId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            // Ищем команду либо по кастомному UUID, либо по встроенному ID Neo4j
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