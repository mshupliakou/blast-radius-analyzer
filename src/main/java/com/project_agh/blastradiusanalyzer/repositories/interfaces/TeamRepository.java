package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Team;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository {
    void addTeam(Team team, String projectId);
    Team getTeamById(String id);
    void deleteTeamById(String id);
}
