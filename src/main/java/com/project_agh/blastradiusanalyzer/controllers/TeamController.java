package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.TeamDto;
import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

/**
 * REST controller for team management within a project.
 * <p>
 * Provides endpoints for creating, retrieving, and deleting teams.
 * The {@code Project-Id} header is required for creation to scope the
 * team to the correct project.
 * </p>
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    /**
     * Constructs the controller with the required team service.
     *
     * @param teamService the service handling team business logic
     */
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Creates a new team within the specified project.
     *
     * @param teamDto   the team payload containing the name
     * @param projectId the project identifier from the header
     * @param principal the authenticated user's principal
     * @return {@code 200 OK} on success
     */
    @PostMapping
    ResponseEntity<?> createTeam(@RequestBody TeamDto teamDto, @RequestHeader("Project-Id") String projectId, Principal principal) {
        Team team = new Team(UUID.randomUUID().toString(), teamDto.name());
        teamService.createTeam(team, projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a team by its unique identifier.
     *
     * @param id the team identifier (path variable)
     * @return the team, or {@code null} if not found
     */
    @GetMapping("/{id}")
    Team getTeam(@PathVariable String id) {
        return teamService.getTeam(id);
    }

    /**
     * Deletes a team by its unique identifier.
     *
     * @param id the team identifier (path variable)
     * @return {@code 200 OK} on success
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTeam(@PathVariable String id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

}
