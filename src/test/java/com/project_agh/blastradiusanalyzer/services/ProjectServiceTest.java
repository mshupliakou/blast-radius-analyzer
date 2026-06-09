package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Project;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProjectService} using Mockito.
 * <p>
 * Verifies the service delegates correctly to {@link ProjectRepository}
 * for creating, listing, and deleting projects, and that users only see
 * their own projects.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    private ProjectService service;

    @BeforeEach
    void setUp() {
        service = new ProjectService(repository);
    }

    /** Creating a project delegates to the repository and returns the created project. */
    @Test
    void createsProjectForUser() {
        Project expected = new Project("p1", "My Project", "", "alice");
        when(repository.createProject("My Project", "alice")).thenReturn(expected);

        Project result = service.createProject("My Project", "alice");

        assertEquals("My Project", result.name());
        assertEquals("alice", result.ownerId());
        verify(repository).createProject("My Project", "alice");
    }

    /** Listing projects returns all projects owned by the user. */
    @Test
    void returnsProjectsForUser() {
        List<Project> projects = List.of(
                new Project("p1", "Proj A", "", "alice"),
                new Project("p2", "Proj B", "", "alice")
        );
        when(repository.getUserProjects("alice")).thenReturn(projects);

        List<Project> result = service.getUserProjects("alice");

        assertEquals(2, result.size());
        verify(repository).getUserProjects("alice");
    }

    /** A user with no projects receives an empty list. */
    @Test
    void emptyListWhenNoProjects() {
        when(repository.getUserProjects("newbie")).thenReturn(List.of());

        List<Project> result = service.getUserProjects("newbie");

        assertTrue(result.isEmpty());
    }

    /** Deleting a project delegates to the repository with the correct parameters. */
    @Test
    void deletesOwnProject() {
        service.deleteProject("p1", "alice");
        verify(repository).deleteProject("p1", "alice");
    }

    /** Users cannot see each other's projects (isolation). */
    @Test
    void projectIsolationDifferentUsers() {
        when(repository.getUserProjects("alice")).thenReturn(List.of(
                new Project("p1", "Alice's", "", "alice")
        ));
        when(repository.getUserProjects("bob")).thenReturn(List.of());

        List<Project> aliceProjects = service.getUserProjects("alice");
        List<Project> bobProjects = service.getUserProjects("bob");

        assertEquals(1, aliceProjects.size());
        assertTrue(bobProjects.isEmpty());
    }
}
