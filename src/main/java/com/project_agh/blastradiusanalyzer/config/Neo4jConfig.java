package com.project_agh.blastradiusanalyzer.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "neo4j.enabled", havingValue = "true", matchIfMissing = true)
public class Neo4jConfig {

    @Value("${NEO4J_URI:neo4j://localhost:7687}")
    private String uri;

    @Value("${NEO4J_USERNAME:neo4j}")
    private String username;

    @Value("${NEO4J_PASSWORD:neo4j}")
    private String password;

    @Value("${NEO4J_DATABASE:microservices}")
    private String databaseName;

    @Bean(destroyMethod = "close")
    public Driver neo4jDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Bean
    public SessionConfig sessionConfig() {
        return SessionConfig.forDatabase(databaseName);
    }
}