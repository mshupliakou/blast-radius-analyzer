package com.project_agh.blastradiusanalyzer.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

    @Value("${NEO4J_URI}")
    private String uri;

    @Value("${NEO4J_USERNAME}")
    private String username;

    @Value("${NEO4J_PASSWORD}")
    private String password;

    @Value("${NEO4J_DATABASE}")
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