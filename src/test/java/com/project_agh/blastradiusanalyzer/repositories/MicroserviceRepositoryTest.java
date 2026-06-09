package com.project_agh.blastradiusanalyzer.repositories;

import com.project_agh.blastradiusanalyzer.models.Microservice;
import com.project_agh.blastradiusanalyzer.models.Project;
import com.project_agh.blastradiusanalyzer.models.User;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.MicroserviceRepository;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.ProjectRepository;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class MicroserviceRepositoryTest {

    @Container
    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5")
            .withAdminPassword("testpass");

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("neo4j.enabled", () -> "true");
        registry.add("NEO4J_URI", neo4j::getBoltUrl);
        registry.add("NEO4J_USERNAME", () -> "neo4j");
        registry.add("NEO4J_PASSWORD", () -> "testpass");
    }

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MicroserviceRepository microserviceRepository;

    @Autowired
    private UserRepository userRepository;

    private String projectId;

    @BeforeEach
    void setUp() {
        userRepository.saveUser(new User("testuser", "testuser", "none"));
        Project project = projectRepository.createProject("Test Project", "testuser");
        projectId = project.id();
    }

    @Test
    void createsAndFindsMicroservice() {
        Microservice ms = new Microservice(UUID.randomUUID().toString(), "Auth Service", "Java");
        microserviceRepository.addMicroservice(ms, projectId);

        Map<String, Object> topology = microserviceRepository.getTopology(projectId);
        List<Map<String, String>> nodes = (List<Map<String, String>>) topology.get("nodes");

        assertTrue(nodes.stream().anyMatch(n -> "Auth Service".equals(n.get("label"))));
    }

    @Test
    void createsDependency() {
        Microservice a = new Microservice(UUID.randomUUID().toString(), "Service A", "Java");
        Microservice b = new Microservice(UUID.randomUUID().toString(), "Service B", "Java");
        microserviceRepository.addMicroservice(a, projectId);
        microserviceRepository.addMicroservice(b, projectId);

        microserviceRepository.createDependency(a.id(), b.id(), projectId);

        List<Microservice> affected = microserviceRepository.getBlastRadius(b.id(), projectId);
        assertTrue(affected.stream().anyMatch(s -> s.id().equals(a.id())));
    }

    @Test
    void blastRadiusChain() {
        Microservice a = new Microservice(UUID.randomUUID().toString(), "A", "Java");
        Microservice b = new Microservice(UUID.randomUUID().toString(), "B", "Java");
        Microservice c = new Microservice(UUID.randomUUID().toString(), "C", "Java");
        microserviceRepository.addMicroservice(a, projectId);
        microserviceRepository.addMicroservice(b, projectId);
        microserviceRepository.addMicroservice(c, projectId);

        microserviceRepository.createDependency(a.id(), b.id(), projectId);
        microserviceRepository.createDependency(b.id(), c.id(), projectId);

        List<Microservice> affected = microserviceRepository.getBlastRadius(c.id(), projectId);
        assertTrue(affected.stream().anyMatch(s -> s.id().equals(a.id())));
        assertTrue(affected.stream().anyMatch(s -> s.id().equals(b.id())));
    }

    @Test
    void deletesNode() {
        Microservice ms = new Microservice(UUID.randomUUID().toString(), "To Delete", "Go");
        microserviceRepository.addMicroservice(ms, projectId);

        microserviceRepository.deleteNode(ms.id(), projectId);

        Map<String, Object> topology = microserviceRepository.getTopology(projectId);
        List<Map<String, String>> nodes = (List<Map<String, String>>) topology.get("nodes");
        assertTrue(nodes.stream().noneMatch(n -> "To Delete".equals(n.get("label"))));
    }

    @Test
    void deletesDependency() {
        Microservice a = new Microservice(UUID.randomUUID().toString(), "A", "Java");
        Microservice b = new Microservice(UUID.randomUUID().toString(), "B", "Java");
        microserviceRepository.addMicroservice(a, projectId);
        microserviceRepository.addMicroservice(b, projectId);
        microserviceRepository.createDependency(a.id(), b.id(), projectId);

        microserviceRepository.deleteDependency(a.id(), b.id(), projectId);

        List<Microservice> affected = microserviceRepository.getBlastRadius(b.id(), projectId);
        assertTrue(affected.isEmpty());
    }

    @Test
    void projectIsolation() {
        User user2 = new User("otheruser", "otheruser", "none");
        userRepository.saveUser(user2);
        Project otherProject = projectRepository.createProject("Other", "otheruser");

        Microservice ms = new Microservice(UUID.randomUUID().toString(), "Secret Service", "Java");
        microserviceRepository.addMicroservice(ms, projectId);

        Map<String, Object> otherTopology = microserviceRepository.getTopology(otherProject.id());
        List<Map<String, String>> otherNodes = (List<Map<String, String>>) otherTopology.get("nodes");
        assertTrue(otherNodes.stream().noneMatch(n -> "Secret Service".equals(n.get("label"))));
    }

    @Test
    void createsClusterWithNodes() {
        Microservice a = new Microservice(UUID.randomUUID().toString(), "A", "Java");
        Microservice b = new Microservice(UUID.randomUUID().toString(), "B", "Python");
        microserviceRepository.addMicroservice(a, projectId);
        microserviceRepository.addMicroservice(b, projectId);

        microserviceRepository.createCluster("My Cluster", "#ff0000", List.of(a.id(), b.id()), projectId);

        Map<String, Object> topology = microserviceRepository.getTopology(projectId);
        List<Map<String, String>> nodes = (List<Map<String, String>>) topology.get("nodes");
        for (Map<String, String> n : nodes) {
            if (n.get("id").equals(a.id()) || n.get("id").equals(b.id())) {
                assertEquals("My Cluster", n.get("clusterName"));
            }
        }
    }
}
