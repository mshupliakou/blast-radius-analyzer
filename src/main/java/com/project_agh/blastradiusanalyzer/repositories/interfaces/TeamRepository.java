package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.Team;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing teams within a project.
 */
@Repository
public interface TeamRepository {
    /** Adds a new team and links it to the given project. */
    void addTeam(Team team, String projectId);

    /** Retrieves a team by its unique identifier. */
    Team getTeamById(String id);

    /** Deletes a team and detaches its relationships. */
    void deleteTeamById(String id);
}
