package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.TeamRepository;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.springframework.stereotype.Repository;

@Repository
class TeamRepositoryImpl implements TeamRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public TeamRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }
    @Override
    public void addTeam(Team team, String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = """
                MATCH (p:Project {id: $projectId})
                MERGE (t:Team {id: $id})
                SET t.name = $name
                MERGE (t)-[:IN_PROJECT]->(p)
                """;

            session.run(cypher, Values.parameters(
                    "id", team.id(),
                    "name", team.name(),
                    "projectId", projectId
            ));
        }
    }

    @Override
    public Team getTeamById(String id) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (t:Team {id: $id}) RETURN t.id AS id, t.name AS name";

            var result = session.run(cypher, Values.parameters("id", id));

            if (result.hasNext()) {
                Record record = result.next();

                return new Team(
                        record.get("id").asString(),
                        record.get("name").asString()
                );
            }

            return null;
        }
    }

    @Override
    public void deleteTeamById(String id) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (t:Team {id: $id}) DETACH DELETE t";
            session.run(cypher, Values.parameters("id", id));
        }
    }
}
