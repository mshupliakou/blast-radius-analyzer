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

            // 1. Микросервисы
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

            // 2. Нотатки
            String noteCypher = "MATCH (n:Note) OPTIONAL MATCH (n)-[:BELONGS_TO]->(c:Cluster) RETURN n.id AS id, n.title AS title, n.text AS text, n.color AS color, c.id AS clusterId, c.name AS clusterName, c.color AS clusterColor";
            var noteResult = session.run(noteCypher);
            while (noteResult.hasNext()) {
                Record record = noteResult.next();
                Map<String, String> nodeData = new java.util.HashMap<>();
                nodeData.put("id", record.get("id").asString());
                nodeData.put("title", record.get("title").asString());
                nodeData.put("text", record.get("text").asString());
                nodeData.put("color", record.get("color").asString());
                nodeData.put("type", "NOTE");
                if (!record.get("clusterId").isNull()) {
                    nodeData.put("clusterId", record.get("clusterId").asString());
                    nodeData.put("clusterName", record.get("clusterName").asString());
                    nodeData.put("clusterColor", record.get("clusterColor").asString());
                }
                nodes.add(nodeData);
            }

            // 3. Команды (Универсальное извлечение ID для совместимости с TeamController)
            String teamCypher = "MATCH (t:Team) RETURN coalesce(toString(t.id), toString(id(t))) AS id, coalesce(t.name, 'Unnamed Team') AS name";
            var teamResult = session.run(teamCypher);
            while (teamResult.hasNext()) {
                Record r = teamResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString());
                n.put("label", r.get("name").asString());
                n.put("group", "Team");
                n.put("type", "TEAM");
                nodes.add(n);
            }

            // 4. Работники (Универсальное извлечение ID)
            String workerCypher = "MATCH (w:Worker) RETURN coalesce(toString(w.id), toString(id(w))) AS id, coalesce(w.name, 'Unknown') AS name, coalesce(w.role, 'Worker') AS role";
            var workerResult = session.run(workerCypher);
            while (workerResult.hasNext()) {
                Record r = workerResult.next();
                Map<String, String> n = new java.util.HashMap<>();
                n.put("id", r.get("id").asString());
                n.put("label", r.get("name").asString());
                n.put("group", r.get("role").asString());
                n.put("type", "WORKER");
                nodes.add(n);
            }

            // 5. Все связи (Универсальное извлечение любых типов связей, включая WORKS_IN и MAINTAINED_BY)
            String edgesCypher = "MATCH (source)-[r]->(target) RETURN coalesce(toString(source.id), toString(id(source))) AS from, coalesce(toString(target.id), toString(id(target))) AS to, type(r) AS type";
            var edgesResult = session.run(edgesCypher);
            List<Map<String, String>> edges = new ArrayList<>();
            while (edgesResult.hasNext()) {
                Record record = edgesResult.next();
                edges.add(java.util.Map.of(
                        "from", record.get("from").asString(),
                        "to", record.get("to").asString(),
                        "type", record.get("type").asString()
                ));
            }

            return java.util.Map.of("nodes", nodes, "edges", edges);
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

    @Override
    public void assignTeamToService(String serviceId, String teamId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (m:Microservice {id: $serviceId})
                MATCH (t:Team)
                WHERE toString(t.id) = $teamId OR toString(id(t)) = $teamId
                MERGE (m)-[:MAINTAINED_BY]->(t)
                """;
            session.run(cypher, Values.parameters("serviceId", serviceId, "teamId", teamId));
        }
    }
}