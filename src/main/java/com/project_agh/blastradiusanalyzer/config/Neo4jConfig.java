package com.project_agh.blastradiusanalyzer.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that creates the Neo4j graph database driver and
 * session configuration beans.
 * <p>
 * Connection details are obtained from environment variables with sensible
 * defaults for local development. This configuration is skipped when the
 * property {@code neo4j.enabled} is explicitly set to {@code false},
 * allowing test profiles to substitute a mock driver.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "neo4j.enabled", havingValue = "true", matchIfMissing = true)
public class Neo4jConfig {

    @Value("${NEO4J_URI:neo4j://localhost:7687}")
    private String uri;

    @Value("${NEO4J_USERNAME:neo4j}")
    private String username;

    @Value("${NEO4J_PASSWORD:keyhes567}")
    private String password;

    @Value("${NEO4J_DATABASE:microservices}")
    private String databaseName;

    /**
     * Creates and configures the Neo4j {@link Driver} bean.
     * <p>
     * The driver is closed automatically when the application context shuts
     * down via the {@code destroyMethod = "close"} attribute.
     * </p>
     *
     * @return a configured Neo4j driver instance
     */
    @Bean(destroyMethod = "close")
    public Driver neo4jDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    /**
     * Provides a {@link SessionConfig} that targets the configured database.
     *
     * @return a session configuration pointing to the target database name
     */
    @Bean
    public SessionConfig sessionConfig() {
        return SessionConfig.forDatabase(databaseName);
    }
}