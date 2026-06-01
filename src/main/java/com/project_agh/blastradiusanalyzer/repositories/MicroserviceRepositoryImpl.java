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
            List<Map<String, String>> nodes = new ArrayList<>();

            String msCypher = "MATCH (m:Microservice) OPTIONAL MATCH (m)-[:BELONGS_TO]->(c:Cluster) RETURN m.id AS id, m.name AS label, m.language AS group, c.id AS clusterId, c.name AS clusterName, c.color AS clusterColor";
            var msResult = session.run(msCypher);
            while (msResult.hasNext()) {
                Record record = msResult.next();
                Map<String, String> nodeData = new java.util.HashMap<>();
                nodeData.put("id", record.get("id").asString());
                nodeData.put("label", record.get("label").asString());
                nodeData.put("group", record.get("group").asString());
                nodeData.put("type", "SERVICE");
                if (!record.get("clusterId").isNull()) {
                    nodeData.put("clusterId", record.get("clusterId").asString());
                    nodeData.put("clusterName", record.get("clusterName").asString());
                    nodeData.put("clusterColor", record.get("clusterColor").asString());
                }
                nodes.add(nodeData);
            }

            String noteCypher = "MATCH (n:Note) OPTIONAL MATCH (n)-[:BELONGS_TO]->(c:Cluster) RETURN n.id AS id, n.title AS title, n.text AS text, n.color AS color, c.id AS clusterId, c.name AS clusterName, c.color AS clusterColor";
            var noteResult = session.run(noteCypher);
            while (noteResult.hasNext()) {
                Record record = noteResult.next();
                Map<String, String> nodeData = new java.util.HashMap<>();
                nodeData.put("id", record.get("id").asString());
                nodeData.put("title", record.get("title").asString());
                nodeData.put("text", record.get("text").asString());
                nodeData.put("color", record.get("color").asString());
                nodeData.put("type", "NOTE"); // Помечаем, что это нотатка
                if (!record.get("clusterId").isNull()) {
                    nodeData.put("clusterId", record.get("clusterId").asString());
                    nodeData.put("clusterName", record.get("clusterName").asString());
                    nodeData.put("clusterColor", record.get("clusterColor").asString());
                }
                nodes.add(nodeData);
            }

            String edgesCypher = "MATCH (source)-[r:DEPENDS_ON|RELATES_TO]->(target) RETURN source.id AS from, target.id AS to, type(r) AS type";
            var edgesResult = session.run(edgesCypher);
            List<Map<String, String>> edges = new ArrayList<>();
            while (edgesResult.hasNext()) {
                Record record = edgesResult.next();
                edges.add(java.util.Map.of("from", record.get("from").asString(), "to", record.get("to").asString(), "type", record.get("type").asString()));
            }
            return java.util.Map.of("nodes", nodes, "edges", edges);
        }
    }

    @Override
    public void deleteMicroservice(String id) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (m:Microservice {id: $id}) DETACH DELETE m";
            session.run(cypher, Values.parameters("id", id));
        }
    }

    @Override
    public void deleteDependency(String sourceId, String targetId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (source {id: $sourceId})-[r:DEPENDS_ON]->(target {id: $targetId})
                DELETE r
                """;
            session.run(cypher, Values.parameters(
                    "sourceId", sourceId,
                    "targetId", targetId
            ));
        }
    }

    @Override
    public void createCluster(String name, String color, List<String> nodeIds) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MERGE (c:Cluster {id: randomUUID()})
                SET c.name = $name, c.color = $color
                WITH c
                
                UNWIND $nodeIds AS nodeId
                MATCH (m:Microservice {id: nodeId})
                
                OPTIONAL MATCH (m)-[old:BELONGS_TO]->(:Cluster)
                DELETE old
                
                MERGE (m)-[:BELONGS_TO]->(c)
                """;
            session.run(cypher, Values.parameters(
                    "name", name,
                    "color", color,
                    "nodeIds", nodeIds
            ));
        }
    }
    @Override
    public void deleteNode(String id) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (n {id: $id}) DETACH DELETE n";
            session.run(cypher, Values.parameters("id", id));
        }
    }

    @Override
    public void addNote(String title, String text, String color, String targetType, String targetId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "CREATE (n:Note {id: randomUUID(), title: $title, text: $text, color: $color}) ";

            if ("SERVICE".equals(targetType) && targetId != null) {
                cypher += "WITH n MATCH (t:Microservice {id: $targetId}) CREATE (n)-[:RELATES_TO]->(t)";
            }

            else if ("CLUSTER".equals(targetType) && targetId != null) {
                cypher += "WITH n MATCH (t:Cluster {id: $targetId}) CREATE (n)-[:BELONGS_TO]->(t)";
            }

            session.run(cypher, Values.parameters("title", title, "text", text, "color", color, "targetId", targetId));
        }
    }
}