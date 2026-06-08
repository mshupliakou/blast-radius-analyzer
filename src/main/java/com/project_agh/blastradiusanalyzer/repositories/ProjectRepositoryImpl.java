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

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public ProjectRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public Project createProject(String name, String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (u:User {username: $username}) CREATE (p:Project {id: randomUUID(), name: $name})-[:OWNED_BY]->(u) RETURN p.id as id, p.name as name";
            Record r = session.run(cypher, Values.parameters("name", name, "username", username)).single();
            return new Project(r.get("id").asString(), r.get("name").asString(), "", "");
        }
    }

    @Override
    public List<Project> getUserProjects(String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (p:Project)-[:OWNED_BY]->(u:User {username: $username}) RETURN p.id as id, p.name as name";
            var res = session.run(cypher, Values.parameters("username", username));
            List<Project> list = new ArrayList<>();
            while (res.hasNext()) {
                Record r = res.next();
                list.add(new Project(r.get("id").asString(), r.get("name").asString(), "", ""));
            }
            return list;
        }
    }
}