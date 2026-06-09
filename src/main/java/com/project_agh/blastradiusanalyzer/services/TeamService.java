package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.models.Team;
import com.project_agh.blastradiusanalyzer.repositories.interfaces.TeamRepository;
import org.springframework.stereotype.Service;

/**
 * Service layer for team management operations.
 * <p>
 * Delegates all data access to {@link TeamRepository} and provides
 * methods for creating, retrieving, and deleting teams.
 * </p>
 */
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    /**
     * Constructs the service with the required repository dependency.
     *
     * @param teamRepository the team repository to delegate to
     */
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /** Creates a new team and links it to the specified project. */
    public void createTeam(Team team, String projectId) {
        teamRepository.addTeam(team, projectId);
    }

    /** Retrieves a team by its unique identifier. */
    public Team getTeam(String id) {
        return teamRepository.getTeamById(id);
    }

    /** Deletes a team by its unique identifier. */
    public void deleteTeam(String id) {
        teamRepository.deleteTeamById(id);
    }
}
