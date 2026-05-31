package com.project_agh.blastradiusanalyzer.controllers;

import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.TeamRepository;
import com.project_agh.blastradiusanalyzer.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class TeamController {
    private TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("create-team/")
    ResponseEntity<?>  createTeam(@RequestBody String name){
        Team team = new Team(String.valueOf(UUID.randomUUID()), name);
        teamService.createTeam(team);
        return ResponseEntity.ok().build();
    }

    @GetMapping("team/{id}")
    Team  getTeam(@PathVariable String id){
        return teamService.getTeam(id);
    }

    @DeleteMapping("team/{id}")
    ResponseEntity<?> deleteTeam(@PathVariable String id){
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

}
