package com.project_agh.blastradiusanalyzer;

import com.project_agh.blastradiusanalyzer.config.TestNeo4jConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mock-neo4j")
@Import(TestNeo4jConfig.class)
class BlastRadiusAnalyzerApplicationTests {

    @Test
    void contextLoads() {
    }

}
