package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.Project;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.ProjectRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Neo4j-backed implementation of {@link ProjectRepository}.
 * <p>
 * Projects are stored as nodes with an {@code OWNED_BY} relationship to
 * the owning user. All queries verify ownership before performing
 * mutations.
 * </p>
 */
@Repository
public class ProjectRepositoryImpl implements ProjectRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    /**
     * Constructs the repository with the Neo4j driver and session configuration.
     *
     * @param driver        the Neo4j driver instance
     * @param sessionConfig the session configuration targeting the correct database
     */
    public ProjectRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public Project createProject(String name, String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (u:User {username: $username}) CREATE (p:Project {id: randomUUID(), name: $name, ownerId: $username})-[:OWNED_BY]->(u) RETURN p.id as id, p.name as name, p.ownerId as ownerId";
            Record r = session.run(cypher, Values.parameters("name", name, "username", username)).single();
            return new Project(r.get("id").asString(), r.get("name").asString(), "", r.get("ownerId").asString());
        }
    }

    @Override
    public List<Project> getUserProjects(String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (p:Project)-[:OWNED_BY]->(u:User {username: $username}) RETURN p.id as id, p.name as name, coalesce(p.ownerId, '') as ownerId";
            var res = session.run(cypher, Values.parameters("username", username));
            List<Project> list = new ArrayList<>();
            while (res.hasNext()) {
                Record r = res.next();
                list.add(new Project(r.get("id").asString(), r.get("name").asString(), "", r.get("ownerId").asString()));
            }
            return list;
        }
    }

    @Override
    public void deleteProject(String projectId, String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (p:Project {id: $projectId})-[:OWNED_BY]->(u:User {username: $username}) DETACH DELETE p";
            session.run(cypher, Values.parameters("projectId", projectId, "username", username));
        }
    }
}