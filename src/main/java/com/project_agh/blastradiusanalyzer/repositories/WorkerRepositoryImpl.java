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
    public void addWorker(String name, String role, String teamId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (t:Team {id: $teamId})
                CREATE (w:Worker {id: randomUUID(), name: $name, role: $role})
                CREATE (w)-[:WORKS_IN]->(t)
                """;
            session.run(cypher, Values.parameters("name", name, "role", role, "teamId", teamId));
        }
    }
}