package com.project_agh.blastradiusanalyzer.config;

import org.mockito.Mockito;
import org.neo4j.driver.Driver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestNeo4jConfig {

    @Bean
    @Primary
    public Driver mockNeo4jDriver() {
        return Mockito.mock(Driver.class);
    }
}
