package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.User;
import java.util.Optional;

/**
 * Repository interface for managing user accounts in the Neo4j database.
 */
public interface UserRepository {
    /** Creates or updates a user node identified by username. */
    void saveUser(User user);

    /** Finds a user by their username, or returns empty if not found. */
    Optional<User> findByUsername(String username);
}