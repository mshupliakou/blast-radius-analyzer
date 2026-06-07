package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.User;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.UserRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class UserRepositoryImpl implements UserRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public UserRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void saveUser(User user) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MERGE (u:User {username: $username}) SET u.id = $id, u.password = $password";
            session.run(cypher, Values.parameters("id", user.id(), "username", user.username(), "password", user.password()));
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Session session = driver.session(sessionConfig)) {
            String cypher = "MATCH (u:User {username: $username}) RETURN u.id AS id, u.username AS username, u.password AS password";
            var result = session.run(cypher, Values.parameters("username", username));
            if (result.hasNext()) {
                Record r = result.next();
                return Optional.of(new User(r.get("id").asString(), r.get("username").asString(), r.get("password").asString()));
            }
            return Optional.empty();
        }
    }
}