package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.dtos.TeamDto;
import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    ResponseEntity<?> createTeam(@RequestBody TeamDto teamDto, @RequestHeader("Project-Id") String projectId, Principal principal) {
        Team team = new Team(UUID.randomUUID().toString(), teamDto.name());
        teamService.createTeam(team, projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    Team getTeam(@PathVariable String id) {
        return teamService.getTeam(id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTeam(@PathVariable String id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

}
