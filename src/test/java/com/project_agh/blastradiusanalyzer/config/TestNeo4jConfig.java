package com.project_agh.blastradiusanalyzer.config;

import org.mockito.Mockito;
import org.neo4j.driver.Driver;
import org.neo4j.driver.SessionConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test configuration that provides mocked Neo4j driver and session
 * configuration beans.
 * <p>
 * Activated when the property {@code neo4j.enabled} is set to
 * {@code false}, allowing unit tests to run without a real Neo4j
 * instance.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "neo4j.enabled", havingValue = "false")
public class TestNeo4jConfig {

    /**
     * Provides a mock Neo4j driver for testing.
     *
     * @return a Mockito mock of {@link Driver}
     */
    @Bean
    public Driver neo4jDriver() {
        return Mockito.mock(Driver.class);
    }

    /**
     * Provides a mock session configuration for testing.
     *
     * @return a Mockito mock of {@link SessionConfig}
     */
    @Bean
    public SessionConfig sessionConfig() {
        return Mockito.mock(SessionConfig.class);
    }
}
