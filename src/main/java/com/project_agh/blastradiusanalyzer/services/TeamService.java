package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public void createTeam(Team team, String projectId) {
        teamRepository.addTeam(team, projectId);
    }

    public Team getTeam(String id) {
        return teamRepository.getTeamById(id);
    }

    public void deleteTeam(String id) {
        teamRepository.deleteTeamById(id);
    }
}
