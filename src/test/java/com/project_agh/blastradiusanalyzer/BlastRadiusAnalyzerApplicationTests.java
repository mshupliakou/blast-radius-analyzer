package com.project_agh.blastradiusanalyzer;

import com.project_agh.blastradiusanalyzer.config.TestNeo4jConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestNeo4jConfig.class)
class BlastRadiusAnalyzerApplicationTests {

    @Test
    void contextLoads() {
    }

}
