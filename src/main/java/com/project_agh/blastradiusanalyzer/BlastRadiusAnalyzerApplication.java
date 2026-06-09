package com.project_agh.blastradiusanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Blast Radius Analyzer Spring Boot application.
 * <p>
 * This application provides an interactive topology-mapping tool for
 * microservice architectures. It uses Neo4j as its graph database,
 * Spring Security with JWT for authentication, and exposes a REST API
 * for managing projects, microservices, teams, dependencies, and
 * organizational structures.
 * </p>
 */
@SpringBootApplication
public class BlastRadiusAnalyzerApplication {

    /**
     * Launches the Spring Boot application.
     *
     * @param args command-line arguments passed to the Spring Boot runner
     */
    public static void main(String[] args) {
        SpringApplication.run(BlastRadiusAnalyzerApplication.class, args);
    }

}
