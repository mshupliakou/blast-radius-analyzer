package com.project_agh.blastradiusanalyzer.config;

import org.mockito.Mockito;
import org.neo4j.driver.Driver;
import org.neo4j.driver.SessionConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "neo4j.enabled", havingValue = "false")
public class TestNeo4jConfig {

    @Bean
    public Driver neo4jDriver() {
        return Mockito.mock(Driver.class);
    }

    @Bean
    public SessionConfig sessionConfig() {
        return Mockito.mock(SessionConfig.class);
    }
}
