package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.MicroserviceRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Neo4j-backed implementation of {@link MicroserviceRepository}.
 * <p>
 * All operations are performed using Cypher queries executed against the
 * configured Neo4j database. Each query is scoped to a specific project
 * to enforce data isolation.
 * </p>
 */
@Repository
class MicroserviceRepositoryImpl implements MicroserviceRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    /**
     * Constructs the repository with the Neo4j driver and session configuration.
     *
     * @param driver        the Neo4j driver instance
     * @param sessionConfig the session configuration targeting the correct database
     */
    public MicroserviceRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void addMicroservice(Microservice microservice, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (p:Project {id: $projectId})
                MERGE (m:Microservice {id: $id})
                SET m.name = $name, m.language = $language
                MERGE (m)-[:IN_PROJECT]->(p)
                """;
            session.run(cypher, Values.parameters("projectId", projectId, "id", microservice.id(), "name", microservice.name(), "language", microservice.language()));
        }
    }

    @Override
    public void createDependency(String sourceId, String targetId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (source {id: $sourceId})-[:IN_PROJECT]->(:Project {id: $projectId}), (target {id: $targetId})-[:IN_PROJECT]->(:Project {id: $projectId}) MERGE (source)-[:DEPENDS_ON]->(target)";
            session.run(cypher, Values.parameters("sourceId", sourceId, "targetId", targetId, "projectId", projectId));
        }
    }

    @Override
    public void deleteDependency(String sourceId, String targetId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (source {id: $sourceId})-[r:DEPENDS_ON]->(target {id: $targetId}) DELETE r";
            session.run(cypher, Values.parameters("sourceId", sourceId, "targetId", targetId));
        }
    }

    @Override
    public void deleteNode(String id, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (n {id: $id})-[:IN_PROJECT]->(:Project {id: $projectId}) DETACH DELETE n";
            session.run(cypher, Values.parameters("id", id, "projectId", projectId));
        }
    }

    @Override
    public void createCluster(String name, String color, List<String> nodeIds, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (p:Project {id: $projectId})
                MERGE (c:Cluster {id: randomUUID()})
                SET c.name = $name, c.color = $color
                MERGE (c)-[:IN_PROJECT]->(p)
                WITH c
                UNWIND $nodeIds AS nodeId
                MATCH (m {id: nodeId})-[:IN_PROJECT]->(:Project {id: $projectId})
                OPTIONAL MATCH (m)-[old:BELONGS_TO]->(:Cluster)
                DELETE old
                MERGE (m)-[:BELONGS_TO]->(c)
                """;
            session.run(cypher, Values.parameters("name", name, "color", color, "nodeIds", nodeIds, "projectId", projectId));
        }
    }

    @Override
    public void addNote(String title, String text, String color, String targetType, String targetId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (p:Project {id: $projectId}) CREATE (n:Note {id: randomUUID(), title: $title, text: $text, color: $color})-[:IN_PROJECT]->(p) ";
            if ("SERVICE".equals(targetType) && targetId != null) {
                cypher += "WITH n MATCH (t:Microservice {id: $targetId}) CREATE (n)-[:RELATES_TO]->(t)";
            } else if ("CLUSTER".equals(targetType) && targetId != null) {
                cypher += "WITH n MATCH (t:Cluster {id: $targetId}) CREATE (n)-[:BELONGS_TO]->(t)";
            }
            session.run(cypher, Values.parameters("projectId", projectId, "title", title, "text", text, "color", color, "targetId", targetId));
        }
    }

    @Override
    public void assignTeamToService(String serviceId, String teamId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (m:Microservice {id: $serviceId})-[:IN_PROJECT]->(:Project {id: $projectId})
                MATCH (t:Team)-[:IN_PROJECT]->(:Project {id: $projectId})
                WHERE toString(t.id) = $teamId OR toString(id(t)) = $teamId
                MERGE (m)-[:MAINTAINED_BY]->(t)
                """;
            session.run(cypher, Values.parameters("serviceId", serviceId, "teamId", teamId, "projectId", projectId));
        }
    }

    @Override
    public List<Microservice> getBlastRadius(String targetId, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (m:Microservice)-[:IN_PROJECT]->(:Project {id: $projectId}) MATCH (m)-[:DEPENDS_ON*1..]->(target {id: $targetId}) RETURN DISTINCT m.id AS id, m.name AS name, m.language AS language";
            var result = session.run(cypher, Values.parameters("targetId", targetId, "projectId", projectId));
            List<Microservice> affectedServices = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                affectedServices.add(new Microservice(record.get("id").asString(), record.get("name").asString(), record.get("language").asString()));
            }
            return affectedServices;
        }
    }

    @Override
    public Map<String, Object> getTopology(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            List<Map<String, String>> nodes = new ArrayList<>();

            String msCypher = "MATCH (m:Microservice)-[:IN_PROJECT]->(:Project {id: $projectId}) OPTIONAL MATCH (m)-[:BELONGS_TO]->(c:Cluster) RETURN m.id AS id, m.name AS label, m.language AS group, c.id AS clusterId, c.name AS clusterName, c.color AS clusterColor";
            var msResult = session.run(msCypher, Values.parameters("projectId", projectId));
            while (msResult.hasNext()) {
                Record r = msResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString()); n.put("label", r.get("label").asString()); n.put("group", r.get("group").asString()); n.put("type", "SERVICE");
                if (!r.get("clusterId").isNull()) { n.put("clusterId", r.get("clusterId").asString()); n.put("clusterName", r.get("clusterName").asString()); n.put("clusterColor", r.get("clusterColor").asString()); }
                nodes.add(n);
            }

            String noteCypher = "MATCH (n:Note)-[:IN_PROJECT]->(:Project {id: $projectId}) OPTIONAL MATCH (n)-[:BELONGS_TO]->(c:Cluster) RETURN n.id AS id, n.title AS title, n.text AS text, n.color AS color, c.id AS clusterId, c.name AS clusterName, c.color AS clusterColor";
            var noteResult = session.run(noteCypher, Values.parameters("projectId", projectId));
            while (noteResult.hasNext()) {
                Record r = noteResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString()); n.put("title", r.get("title").asString()); n.put("text", r.get("text").asString()); n.put("color", r.get("color").asString()); n.put("type", "NOTE");
                if (!r.get("clusterId").isNull()) { n.put("clusterId", r.get("clusterId").asString()); n.put("clusterName", r.get("clusterName").asString()); n.put("clusterColor", r.get("clusterColor").asString()); }
                nodes.add(n);
            }

            String teamCypher = "MATCH (t:Team)-[:IN_PROJECT]->(:Project {id: $projectId}) RETURN coalesce(toString(t.id), toString(id(t))) AS id, coalesce(t.name, 'Unnamed Team') AS name";
            var teamResult = session.run(teamCypher, Values.parameters("projectId", projectId));
            while (teamResult.hasNext()) {
                Record r = teamResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString()); n.put("label", r.get("name").asString()); n.put("group", "Team"); n.put("type", "TEAM");
                nodes.add(n);
            }

            String workerCypher = "MATCH (w:Worker)-[:IN_PROJECT]->(:Project {id: $projectId}) RETURN coalesce(toString(w.id), toString(id(w))) AS id, coalesce(w.name, 'Unknown') AS name, coalesce(w.role, 'Worker') AS role";
            var workerResult = session.run(workerCypher, Values.parameters("projectId", projectId));
            while (workerResult.hasNext()) {
                Record r = workerResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString()); n.put("label", r.get("name").asString()); n.put("group", r.get("role").asString()); n.put("type", "WORKER");
                nodes.add(n);
            }

            String edgesCypher = "MATCH (source)-[:IN_PROJECT]->(:Project {id: $projectId}), (target)-[:IN_PROJECT]->(:Project {id: $projectId}), (source)-[r]->(target) WHERE type(r) <> 'IN_PROJECT' AND type(r) <> 'OWNED_BY' RETURN coalesce(toString(source.id), toString(id(source))) AS from, coalesce(toString(target.id), toString(id(target))) AS to, type(r) AS type";
            var edgesResult = session.run(edgesCypher, Values.parameters("projectId", projectId));
            List<Map<String, String>> edges = new ArrayList<>();
            while (edgesResult.hasNext()) {
                Record record = edgesResult.next();
                edges.add(java.util.Map.of("from", record.get("from").asString(), "to", record.get("to").asString(), "type", record.get("type").asString()));
            }

            return java.util.Map.of("nodes", nodes, "edges", edges);
        }
    }
}