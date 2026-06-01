package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.MicroserviceRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
class MicroserviceRepositoryImpl implements MicroserviceRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public MicroserviceRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void addMicroservice(Microservice microservice) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MERGE (m:Microservice {id: $id})
                SET m.name = $name, m.language = $language
                """;
            session.run(cypher, Values.parameters(
                    "id", microservice.id(),
                    "name", microservice.name(),
                    "language", microservice.language()
            ));
        }
    }

    @Override
    public void createDependency(String sourceId, String targetId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (source {id: $sourceId})
                MATCH (target {id: $targetId})
                MERGE (source)-[:DEPENDS_ON]->(target)
                """;
            session.run(cypher, Values.parameters(
                    "sourceId", sourceId,
                    "targetId", targetId
            ));
        }
    }

    @Override
    public List<Microservice> getBlastRadius(String targetId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (m:Microservice)-[:DEPENDS_ON*1..]->(target {id: $targetId})
                RETURN DISTINCT m.id AS id, m.name AS name, m.language AS language
                """;

            var result = session.run(cypher, Values.parameters("targetId", targetId));
            List<Microservice> affectedServices = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                affectedServices.add(new Microservice(
                        record.get("id").asString(),
                        record.get("name").asString(),
                        record.get("language").asString()
                ));
            }
            return affectedServices;
        }
    }

    @Override
    public Map<String, Object> getTopology() {
        try (Session session = driver.session(sessionConfig)) {

            String nodesCypher = "MATCH (m:Microservice) RETURN m.id AS id, m.name AS label, m.language AS group";
            var nodesResult = session.run(nodesCypher);
            List<Map<String, String>> nodes = new ArrayList<>();

            while (nodesResult.hasNext()) {
                Record record = nodesResult.next();
                nodes.add(Map.of(
                        "id", record.get("id").asString(),
                        "label", record.get("label").asString(),
                        "group", record.get("group").asString()
                ));
            }

            String edgesCypher = "MATCH (source:Microservice)-[:DEPENDS_ON]->(target:Microservice) RETURN source.id AS from, target.id AS to";
            var edgesResult = session.run(edgesCypher);
            List<Map<String, String>> edges = new ArrayList<>();

            while (edgesResult.hasNext()) {
                Record record = edgesResult.next();
                edges.add(Map.of(
                        "from", record.get("from").asString(),
                        "to", record.get("to").asString()
                ));
            }

            return Map.of("nodes", nodes, "edges", edges);
        }
    }
}