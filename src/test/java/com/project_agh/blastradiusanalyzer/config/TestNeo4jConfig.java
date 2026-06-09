package com.project_agh.blastradiusanalyzer.config;

import org.mockito.Mockito;
import org.neo4j.driver.Driver;
import org.neo4j.driver.SessionConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("mock-neo4j")
public class TestNeo4jConfig {

    @Bean
    public Driver mockNeo4jDriver() {
        return Mockito.mock(Driver.class);
    }

    @Bean
    public SessionConfig sessionConfig() {
        return Mockito.mock(SessionConfig.class);
    }
}
